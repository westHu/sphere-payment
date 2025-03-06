package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.TradeWithdrawOrderCmdService;
import com.paysphere.command.cmd.TradeWithdrawCommand;
import com.paysphere.command.cmd.TradeWithdrawReviewCommand;
import com.paysphere.command.dto.ReceiverInfoDTO;
import com.paysphere.command.dto.TradeWithdrawAmountDTO;
import com.paysphere.command.dto.trade.result.ReviewResultDTO;
import com.paysphere.command.dto.trade.result.TradeResultDTO;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantWithdrawConfig;
import com.paysphere.db.entity.TradeWithdrawOrder;
import com.paysphere.enums.AreaEnum;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.manager.OrderNoManager;
import com.paysphere.mq.dto.examine.TradeWithdrawExamineMqMessageDTO;
import com.paysphere.mq.dto.settle.UnfrozenMessageDTO;
import com.paysphere.mq.dto.settle.WithdrawMqMessageDTO;
import com.paysphere.repository.TradeWithdrawOrderService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class TradeWithdrawOrderCmdServiceImpl implements TradeWithdrawOrderCmdService {

    @Resource
    TradeWithdrawOrderService tradeWithdrawOrderService;
    @Resource
    RedisService redisService;
    @Resource
    OrderNoManager orderNoManager;

    @Override
    public boolean executeWithdraw(TradeWithdrawCommand command) {
        log.info("executeWithdraw command={}", JSONUtil.toJsonStr(command));
        String merchantId = command.getMerchantId();
        return redisService.lock(TradeConstant.LOCK_PREFIX_WITHDRAW + merchantId,
                () -> doWithdraw(command));
    }


    @Override
    public void executeWithdrawReview(TradeWithdrawReviewCommand command) {
        log.info("executeWithdrawReview command={}", JSONUtil.toJsonStr(command));
        redisService.lock(TradeConstant.LOCK_PREFIX_WITHDRAW_REVIEW + command.getTradeNo(),
                () -> doWithdrawReview(command));
    }


    // ------------------------------------------------------------------------------------------------------

    /**
     * 提现操作
     */
    private boolean doWithdraw(TradeWithdrawCommand command) {
        // 查询商户提现配置 可缓存
        MerchantWithdrawConfig withdrawConfig = getWithdrawConfig(command.getMerchantId());

        // 校验订单 保存订单 - [1]
        TradeWithdrawOrder order = saveTradeWithdrawOrder(command, withdrawConfig);
        String tradeNo = order.getTradeNo();

        // 执行冻结操作, 同步
        withdrawFrozenAmount(order);

        // 发起审核, Mq给管理台, 状态更为审核中
        sendMsgToReview(order, command.getApplyOperator());

        // 更新审核中
        UpdateWrapper<TradeWithdrawOrder> withdrawOrderUpdate = new UpdateWrapper<>();
        withdrawOrderUpdate.lambda()
                .set(TradeWithdrawOrder::getTradeStatus, TradeStatusEnum.TRADE_REVIEW.getCode())
                .eq(TradeWithdrawOrder::getTradeNo, tradeNo);
        return tradeWithdrawOrderService.update(withdrawOrderUpdate);
    }


    /**
     * 查询提现配置
     */
    private MerchantWithdrawConfig getWithdrawConfig(String merchantId) {
        String redisKey = TradeConstant.LOCK_PREFIX_WITHDRAW_CONFIG + merchantId;
        Object obj = redisService.get(redisKey);
        MerchantWithdrawConfig withdrawConfig = Optional.ofNullable(obj).map(Object::toString)
                .map(e -> JSONUtil.toBean(e, MerchantWithdrawConfig.class))
                .orElse(null);
        if (Objects.nonNull(withdrawConfig)) {
            return withdrawConfig;
        }

        /*MerchantIdParam idParam = new MerchantIdParam();
        idParam.setMerchantId(merchantId);

        // 查询服务得到提现配置
        Mono<Result<MerchantWithdrawConfigDTO>> resultMono = merchantApiService.getMerchantWithdrawConfig(idParam);
        Result<MerchantWithdrawConfigDTO> result = resultMono.toFuture().join();
        withdrawConfig = BaseResult.parse(result);
        log.info("getWithdrawConfig withdrawConfig={}", JSONUtil.toJsonStr(withdrawConfig));
        if (Objects.isNull(withdrawConfig)) {
            throw new PaymentException(ExceptionCode.WITHDRAW_CONFIG_NOT_EXIST, merchantId);
        }

        // 校验配置
        String withdrawPaymentMethod = withdrawConfig.getWithdrawPaymentMethod();
        String withdrawAccount = withdrawConfig.getWithdrawAccount();
        Integer deductionType = withdrawConfig.getDeductionType();
        if (StringUtils.isAnyBlank(withdrawPaymentMethod, withdrawAccount) || Objects.isNull(deductionType)) {
            throw new PaymentException(ExceptionCode.WITHDRAW_CONFIG_NOT_EXIST, merchantId);
        }*/

        // 设置到缓存
        redisService.set(redisKey, JSONUtil.toJsonStr(withdrawConfig), 60 * 5);
        return withdrawConfig;
    }

    /**
     * 校验订单 保存订单
     */
    private TradeWithdrawOrder saveTradeWithdrawOrder(TradeWithdrawCommand command,
                                                      MerchantWithdrawConfig withdrawConfig) {
        // 构建金额
        TradeWithdrawAmountDTO amountDTO = parseWithdrawAmount(command, withdrawConfig);

        // 到账金额，可能会因为内扣导致到账金额小于0
        BigDecimal accountAmount = amountDTO.getAccountAmount();
        log.info("saveTradeWithdrawOrder amountDTO={}", JSONUtil.toJsonStr(amountDTO));
        if (accountAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("saveTradeWithdrawOrder accountAmount is less than zero.");
            throw new PaymentException(ExceptionCode.WITHDRAW_ACTUAL_AMOUNT_ERROR);
        }

        Merchant merchantBaseDTO = new Merchant();
        merchantBaseDTO.setMerchantId(command.getMerchantId());
        merchantBaseDTO.setArea(AreaEnum.INDONESIA.getCode());

        String tradeNo = orderNoManager.getTradeNo(null, TradeTypeEnum.WITHDRAW, merchantBaseDTO.getMerchantId());

        TradeWithdrawOrder order = new TradeWithdrawOrder();
        order.setBusinessNo(orderNoManager.getBusinessNo());
        order.setTradeNo(tradeNo);
        order.setPurpose(command.getPurpose());
        order.setMerchantId(command.getMerchantId());
        order.setMerchantName(command.getMerchantName());
        order.setAccountNo(command.getAccountNo());

        ReceiverInfoDTO receiverInfoDTO = new ReceiverInfoDTO();
//        receiverInfoDTO.setName(withdrawConfig.getWithdrawPaymentName());// 账户姓名
        order.setReceiverInfo(JSONUtil.toJsonStr(receiverInfoDTO));

        // 金额 实扣金额 商户手续费 商户分润 到账金额 通道成本 平台利润
        order.setCurrency(amountDTO.getCurrency());
        order.setAmount(amountDTO.getWithdrawAmount());
        order.setActualAmount(amountDTO.getActualAmount());
        order.setMerchantProfit(BigDecimal.ZERO);
        order.setMerchantFee(BigDecimal.ZERO);
        order.setAccountAmount(amountDTO.getActualAmount());
        order.setChannelCost(BigDecimal.ZERO);// 走线下
        order.setPlatformProfit(BigDecimal.ZERO);

        order.setAccountAmount(accountAmount);
//        order.setPaymentMethod(withdrawConfig.getWithdrawPaymentMethod());
//        order.setWithdrawAccount(withdrawConfig.getWithdrawAccount());
//        order.setTradeTime(LocalDateTime.now());
        order.setTradeStatus(TradeStatusEnum.TRADE_INIT.getCode());
//        order.setPaymentStatus(PaymentStatusEnum.PAYMENT_UNPAID.getCode());
        order.setSettleStatus(SettleStatusEnum.SETTLE_TODO.getCode());
        order.setCreateTime(LocalDateTime.now());
        tradeWithdrawOrderService.save(order);
        return order;
    }

    /**
     * 计算提现金额
     */
    private TradeWithdrawAmountDTO parseWithdrawAmount(TradeWithdrawCommand command,
                                                       MerchantWithdrawConfig withdrawConfig) {
        String merchantId = command.getMerchantId();

        // 暂定费率为0(收取商户)
//        BigDecimal withdrawRate = Optional.of(withdrawConfig).map(MerchantWithdrawConfig::getWithdrawRate)
//                .orElse(BigDecimal.ZERO);
//        BigDecimal withdrawFee = Optional.of(withdrawConfig).map(MerchantWithdrawConfig::getWithdrawFee)
//                .orElse(BigDecimal.ZERO);
//        Integer deductionType = Optional.of(withdrawConfig)
//                .map(MerchantWithdrawConfigDTO::getDeductionType)
//                .orElseThrow(() -> new PaymentException(ExceptionCode.WITHDRAW_DEDUCTION_NOT_CONFIG, merchantId));

        BigDecimal withdrawAmount = command.getAmount();
//        BigDecimal merchantFee = withdrawAmount.multiply(withdrawRate).add(withdrawFee);
//        DeductionTypeEnum deductionTypeEnum = DeductionTypeEnum.codeToEnum(deductionType);
//        log.info("parse withdrawAmount={}, merchantFee={}, deduction={}", withdrawAmount, merchantFee, deductionTypeEnum);

//        BigDecimal actualAmount;
//        BigDecimal accountAmount;
//        if (DeductionTypeEnum.DEDUCTION_INTERNAL.equals(deductionTypeEnum)) {
//            actualAmount = withdrawAmount;
//            accountAmount = withdrawAmount.subtract(merchantFee);
//        } else if (DeductionTypeEnum.DEDUCTION_EXTERNAL.equals(deductionTypeEnum)) {
//            actualAmount = withdrawAmount.add(merchantFee);
//            accountAmount = withdrawAmount;
//        } else {
//            throw new PaymentException(ExceptionCode.UNSUPPORTED_DEDUCTION_TYPE);
//        }
//
        TradeWithdrawAmountDTO amountDTO = new TradeWithdrawAmountDTO();
//        amountDTO.setCurrency(command.getCurrency());
//        amountDTO.setWithdrawAmount(withdrawAmount);
//        amountDTO.setActualAmount(actualAmount);
//        amountDTO.setAccountAmount(accountAmount);
        return amountDTO;
    }

    /**
     * 冻结商户资金账户
     */
    @SneakyThrows
    private void withdrawFrozenAmount(TradeWithdrawOrder order) {
        /*String tradeNo = order.getTradeNo();

        AccountAmountFrozenParam frozenParam = new AccountAmountFrozenParam();
        frozenParam.setBusinessNo(order.getBusinessNo());
        frozenParam.setTradeNo(tradeNo);
        frozenParam.setCurrency(order.getCurrency());
        frozenParam.setAmount(order.getActualAmount()); // 实扣金额，非交易金额
        frozenParam.setMerchantId(order.getMerchantId());
        frozenParam.setMerchantName(order.getMerchantName());
        frozenParam.setAccountNo(order.getAccountNo());

        try {
            log.info("withdrawFrozenAmount tradeNo={}, result={}", tradeNo, JSONUtil.toJsonStr(frozenParam));
            Boolean withdrawFrozen = BaseResult.parse(settleApiService.frozenAmount(frozenParam).toFuture().join());
            log.info("withdrawFrozenAmount tradeNo={}, result={}", tradeNo, withdrawFrozen);

        } catch (Exception e) {
            log.error("doWithdraw order:{}. failed to frozen amount exception", tradeNo, e);

            // 更新订单状态
            String error = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "Failed to frozen amount";
            TradeResultDTO tradeResultDTO = new TradeResultDTO();
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(error);
            UpdateWrapper<TradeWithdrawOrder> withdrawOrderUpdate = new UpdateWrapper<>();
            withdrawOrderUpdate.lambda()
                    .set(TradeWithdrawOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeWithdrawOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradeWithdrawOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradeWithdrawOrder::getPaymentFinishTime, LocalDateTime.now())
                    .eq(TradeWithdrawOrder::getTradeNo, tradeNo);
            tradeWithdrawOrderService.update(withdrawOrderUpdate);
            throw new PaymentException(ExceptionCode.WITHDRAW_FROZEN_AMOUNT_FAILED, tradeNo);
        }*/

    }


    /**
     * 发起审核
     */
    private void sendMsgToReview(TradeWithdrawOrder order, String applyOperator) {
        String tradeNo = order.getTradeNo();
        String accountName = Optional.of(order).map(TradeWithdrawOrder::getReceiverInfo)
                .map(e -> JSONUtil.toBean(e, ReceiverInfoDTO.class))
                .map(ReceiverInfoDTO::getName)
                .orElse(null);

        TradeWithdrawExamineMqMessageDTO messageDTO = new TradeWithdrawExamineMqMessageDTO();
        messageDTO.setBusinessNo(order.getBusinessNo());
        messageDTO.setTradeNo(tradeNo);
//        messageDTO.setTradeTime(order.getTradeTime().format(TradeConstant.DF_0));
        messageDTO.setPurpose(order.getPurpose());
        messageDTO.setMerchantId(order.getMerchantId());
        messageDTO.setMerchantName(order.getMerchantName());
        messageDTO.setAccountNo(order.getAccountNo());
        messageDTO.setCurrency(order.getCurrency());
        messageDTO.setAmount(order.getAmount());
        messageDTO.setActualAmount(order.getActualAmount());
        messageDTO.setAccountAmount(order.getAccountAmount());
        messageDTO.setPaymentMethod(order.getPaymentMethod());
        messageDTO.setWithdrawAccount(order.getWithdrawAccount());
        messageDTO.setAccountName(accountName);
        messageDTO.setApplyOperator(applyOperator);

        try {
//            SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.TRADE_EXAMINE_TOPIC, JSONUtil.toJsonStr(messageDTO));
//            log.info("withdraw sendMsgToReview sendResult={}", sendResult);
        } catch (Exception e) {
            log.error("sendMsgToReview order:{}. failed mq message exception", tradeNo, e);
            // 更新订单状态
            String error = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "Failed to mq message";

            // 解冻资金, 如果此处异常, 放弃吧
            withdrawUnfrozenAmount(order);

            // 更新交易订单：交易状态
            TradeResultDTO tradeResultDTO = new TradeResultDTO();
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(error);
            UpdateWrapper<TradeWithdrawOrder> withdrawOrderUpdate = new UpdateWrapper<>();
            withdrawOrderUpdate.lambda()
                    .set(TradeWithdrawOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeWithdrawOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradeWithdrawOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradeWithdrawOrder::getPaymentFinishTime, LocalDateTime.now())
                    .eq(TradeWithdrawOrder::getTradeNo, tradeNo);
            tradeWithdrawOrderService.update(withdrawOrderUpdate);
            throw new PaymentException(ExceptionCode.WITHDRAW_CHANNEL_ERROR, tradeNo, error);
        }

    }

    /**
     * 解冻商户资金账户
     */
    private void withdrawUnfrozenAmount(TradeWithdrawOrder order) {
        String tradeNo = order.getTradeNo();
        UnfrozenMessageDTO unfrozenMessageDTO = new UnfrozenMessageDTO();
        unfrozenMessageDTO.setTradeNo(tradeNo);
        unfrozenMessageDTO.setOuterNo(tradeNo);

//        SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.UNFROZEN_TOPIC, JSONUtil.toJsonStr(unfrozenMessageDTO));
//        log.info("withdrawUnfrozenAmount tradeNo={}, result={}", tradeNo, sendResult);
    }

    /**
     * 提现审核
     */
    private boolean doWithdrawReview(TradeWithdrawReviewCommand command) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradeWithdrawOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeWithdrawOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeWithdrawOrder order = tradeWithdrawOrderService.getOne(orderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.WITHDRAW_ORDER_NOT_EXIST, tradeNo));

        // 判断是否在审核中状态
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!TradeStatusEnum.TRADE_REVIEW.equals(tradeStatusEnum)) {
            throw new PaymentException(ExceptionCode.WITHDRAW_ORDER_NOT_IN_REVIEW, tradeNo);
        }

        // 先解析TradeResult 构建审核结果
        TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradeWithdrawOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());
        ReviewResultDTO reviewResultDTO = new ReviewResultDTO();
        reviewResultDTO.setReviewStatus(command.isReviewStatus());
        reviewResultDTO.setReviewTime(LocalDateTime.now());
        reviewResultDTO.setReviewMsg(command.getReviewMsg());
        tradeResultDTO.setReviewResult(reviewResultDTO);

        // 审核驳回
        if (!command.isReviewStatus()) {
            // 设置失败
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(command.getReviewMsg());

            // 解冻资金
            withdrawUnfrozenAmount(order);

            // 更新订单状态
            UpdateWrapper<TradeWithdrawOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(TradeWithdrawOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeWithdrawOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradeWithdrawOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradeWithdrawOrder::getPaymentFinishTime, LocalDateTime.now())
                    .eq(TradeWithdrawOrder::getId, order.getId());
            return tradeWithdrawOrderService.update(updateWrapper);
        }

        // 审核通过 处理转账资金操作, FIX 比较依赖中间件MQ的稳定性
        sendMsgToWithdraw(order);

        // 审核通过 处理提现订单
        tradeResultDTO.setSuccess(true);
        UpdateWrapper<TradeWithdrawOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(TradeWithdrawOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(TradeWithdrawOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .set(TradeWithdrawOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_SUCCESS.getCode())
                .set(TradeWithdrawOrder::getPaymentFinishTime, LocalDateTime.now())
                .eq(TradeWithdrawOrder::getId, order.getId());
        tradeWithdrawOrderService.update(updateWrapper);

        // 根据情况发送邮箱
        // sendMsgToEmail(order);
        return true;
    }

    /**
     * 提交清结算操作
     */
    private void sendMsgToWithdraw(TradeWithdrawOrder order) {
        WithdrawMqMessageDTO messageDTO = new WithdrawMqMessageDTO();
        messageDTO.setBusinessNo(order.getBusinessNo());
        messageDTO.setTradeNo(order.getTradeNo());
//        messageDTO.setTradeTime(order.getTradeTime().format(TradeConstant.DF_0));
        messageDTO.setMerchantId(order.getMerchantId());
        messageDTO.setMerchantName(order.getMerchantName());
        messageDTO.setAccountNo(order.getAccountNo());
        messageDTO.setCurrency(order.getCurrency());
        messageDTO.setAmount(order.getActualAmount());  // 实扣金额，非交易金额
        messageDTO.setMerchantFee(order.getMerchantFee()); // 手续费

        try {
            log.info("doWithdrawReview messageDTO={}", JSONUtil.toJsonStr(messageDTO));
//            SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.WITHDRAW_TOPIC, JSONUtil.toJsonStr(messageDTO));
//            log.info("doWithdrawReview sendResult={}", sendResult);
        } catch (Exception e) {
            log.error("doWithdrawReview exception:", e);
            String error = StringUtils.isNoneBlank(e.getMessage()) ? e.getMessage() : "handler review error";

            // 解冻资金
            withdrawUnfrozenAmount(order);

            // 设置失败
            TradeResultDTO tradeResultDTO = new TradeResultDTO();
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(error);
            UpdateWrapper<TradeWithdrawOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(TradeWithdrawOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeWithdrawOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradeWithdrawOrder::getPaymentFinishTime, LocalDateTime.now())
                    .set(TradeWithdrawOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .eq(TradeWithdrawOrder::getId, order.getId());
            tradeWithdrawOrderService.update(updateWrapper);
            throw new PaymentException(ExceptionCode.WITHDRAW_CHANNEL_ERROR, order.getTradeNo(), error);
        }
    }

}
