package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.TradeRechargeOrderCmdService;
import com.paysphere.command.cmd.TradePreRechargeCommand;
import com.paysphere.command.cmd.TradeRechargeCommand;
import com.paysphere.command.cmd.TradeRechargeReviewCommand;
import com.paysphere.command.dto.PreRechargeDTO;
import com.paysphere.command.dto.TradeRechargeAttributeDTO;
import com.paysphere.command.dto.trade.result.ReviewResultDTO;
import com.paysphere.command.dto.trade.result.TradeResultDTO;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.TradeRechargeOrder;
import com.paysphere.enums.AreaEnum;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.manager.OrderNoManager;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.mq.dto.examine.TradeRechargeExamineMqMessageDTO;
import com.paysphere.mq.dto.settle.RechargeMqMessageDTO;
import com.paysphere.repository.TradeRechargeOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class TradeRechargeOrderCmdServiceImpl implements TradeRechargeOrderCmdService {

    @Resource
    RedisService redisService;
    @Resource
    RocketMqProducer rocketMqProducer;
    @Resource
    TradeRechargeOrderService tradeRechargeOrderService;
    @Resource
    OrderNoManager orderNoManager;


    @Override
    public PreRechargeDTO executePreRecharge(TradePreRechargeCommand command) {
        log.info("executePreRecharge command={}", JSONUtil.toJsonStr(command));
        return redisService.lock(TradeConstant.LOCK_PREFIX_RECHARGE + command.getMerchantId(), () -> doPreRecharge(command));
    }


    @Override
    public boolean executeRecharge(TradeRechargeCommand command) {
        log.info("executePreRecharge command={}", JSONUtil.toJsonStr(command));
        return redisService.lock(TradeConstant.LOCK_PREFIX_RECHARGE + command.getTradeNo(), () -> doRecharge(command));
    }


    @Override
    public void executeRechargeReview(TradeRechargeReviewCommand command) {
        log.info("executeRechargeReview command={}", JSONUtil.toJsonStr(command));
        redisService.lock(TradeConstant.LOCK_PREFIX_RECHARGE + command.getTradeNo(), () -> doRechargeReview(command));
    }


    @Override
    public boolean reviewRecharge(String tradeNo) {
        log.info("reviewRecharge tradeNo={}", tradeNo);
        return redisService.lock(TradeConstant.LOCK_PREFIX_RECHARGE + tradeNo, () -> doReviewRecharge(tradeNo));
    }


    // ------------------------------------------------------------------------------------------------------

    /**
     * 充值前置操作
     */
    private PreRechargeDTO doPreRecharge(TradePreRechargeCommand command) {
        // 添加随机金额, 默认$10
        BigDecimal randomAmount = BigDecimal.TEN;
        try {
            Random rand = SecureRandom.getInstanceStrong();  // SecureRandom is preferred to Random
            int x = rand.nextInt(900) + 100;
            randomAmount = new BigDecimal(x);
        } catch (Exception e) {
            log.error("SecureRandom amount exception", e);
        }
        log.info("doPreRecharge randomAmount={}", randomAmount);

        // 根据支付方式得到账户号
        String bankAccount;
        String holderName;
        String paymentMethod = command.getPaymentMethod();
        if (StringUtils.equals(paymentMethod, TradeConstant.RECHARGE_BNC)) {
            bankAccount = TradeConstant.RECHARGE_BNC_ACCOUNT;
            holderName = TradeConstant.RECHARGE_BNC_HOLDER_NAME;
        } else {
            throw new PaymentException(ExceptionCode.RECHARGE_UNSUPPORTED_PAYMENT, paymentMethod);
        }

        // 校验订单 保存订单
        TradeRechargeOrder order = saveTradeRechargeOrder(command, randomAmount, bankAccount);

        // 返回
        PreRechargeDTO dto = new PreRechargeDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setHolderName(holderName);
        dto.setBankAccount(order.getBankAccount());
        dto.setAmount(order.getAmount());
        dto.setRandomAmount(randomAmount);
        return dto;
    }

    /**
     * 充值操作
     */
    private boolean doRecharge(TradeRechargeCommand command) {
        // 如果充值成功,请提供证明
        if (command.isStatus() && StringUtils.isBlank(command.getProof())) {
            throw new PaymentException(ExceptionCode.PARAM_IS_REQUIRED, "Recharge proof");
        }

        // 校验订单
        String tradeNo = command.getTradeNo();
        QueryWrapper<TradeRechargeOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeRechargeOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeRechargeOrder order = tradeRechargeOrderService.getOne(orderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.RECHARGE_ORDER_NOT_EXIST, tradeNo));

        // 校验状态必须是初始化
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!TradeStatusEnum.TRADE_INIT.equals(tradeStatusEnum)) {
            throw new PaymentException(ExceptionCode.RECHARGE_STATUS_MUST_BE_INIT, command.getTradeNo());
        }

        // 用户取消
        if (!command.isStatus()) {

            // 更新状态 失败, 原因: 点击取消
            TradeResultDTO tradeResultDTO = new TradeResultDTO();
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError("MERCHANT CANCEL");
            UpdateWrapper<TradeRechargeOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(TradeRechargeOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeRechargeOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .eq(TradeRechargeOrder::getId, order.getId());
            return tradeRechargeOrderService.update(updateWrapper);
        }

        // 发起审核, 此处异常, 状态INIT, 不影响, 则忽略
        sendMsgToReview(order, command.getProof());

        // 证明截图
        TradeRechargeAttributeDTO remarkDTO = new TradeRechargeAttributeDTO();
        remarkDTO.setProof(command.getProof());

        // 更新状态
        TradeResultDTO tradeResultDTO = new TradeResultDTO();
        tradeResultDTO.setSuccess(true);
        UpdateWrapper<TradeRechargeOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(TradeRechargeOrder::getTradeStatus, TradeStatusEnum.TRADE_REVIEW.getCode())
                .set(TradeRechargeOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .set(TradeRechargeOrder::getAttribute, JSONUtil.toJsonStr(remarkDTO))
                .eq(TradeRechargeOrder::getId, order.getId());
        return tradeRechargeOrderService.update(updateWrapper);
    }

    /**
     * 校验订单 保存订单
     */
    private TradeRechargeOrder saveTradeRechargeOrder(TradePreRechargeCommand command,
                                                      BigDecimal randomAmount,
                                                      String bankAccount) {
        Merchant merchantBaseDTO = new Merchant();
        merchantBaseDTO.setMerchantId(command.getMerchantId());
        merchantBaseDTO.setArea(AreaEnum.INDONESIA.getCode());

        String tradeNo = orderNoManager.getTradeNo(null, TradeTypeEnum.RECHARGE, merchantBaseDTO.getMerchantId());
        TradeRechargeOrder order = new TradeRechargeOrder();
        order.setBusinessNo(orderNoManager.getBusinessNo());
        order.setTradeNo(tradeNo);
        order.setPurpose(command.getPurpose());
        order.setMerchantId(command.getMerchantId());
        order.setMerchantName(command.getMerchantName());
        order.setAccountNo(command.getAccountNo());

        // 金额 商户手续费 商户分润 到账金额 通道成本 平台利润
        BigDecimal actualAmount = command.getAmount().add(randomAmount);
        order.setCurrency(command.getCurrency());
        order.setAmount(actualAmount);
        order.setMerchantFee(BigDecimal.ZERO);
        order.setMerchantProfit(BigDecimal.ZERO);
        order.setAccountAmount(actualAmount);
        order.setChannelCost(BigDecimal.ZERO);
        order.setPlatformProfit(BigDecimal.ZERO);

        order.setPaymentMethod(command.getPaymentMethod());
        order.setBankAccount(bankAccount);
        order.setTradeStatus(TradeStatusEnum.TRADE_INIT.getCode());
//        order.setTradeTime(LocalDateTime.now());
        order.setSettleStatus(SettleStatusEnum.SETTLE_TODO.getCode());
        order.setCreateTime(LocalDateTime.now());
        boolean save = tradeRechargeOrderService.save(order);
        log.info("saveTradeRechargeOrder save={}", save);
        return order;
    }

    /**
     * 发起审核
     */
    private void sendMsgToReview(TradeRechargeOrder order, String proof) {
        TradeRechargeExamineMqMessageDTO messageDTO = new TradeRechargeExamineMqMessageDTO();
        messageDTO.setBusinessNo(order.getBusinessNo());
        messageDTO.setTradeNo(order.getTradeNo());
//        messageDTO.setTradeTime(order.getTradeTime().format(TradeConstant.DF_0));
        messageDTO.setPurpose(order.getPurpose());
        messageDTO.setMerchantId(order.getMerchantId());
        messageDTO.setMerchantName(order.getMerchantName());
        messageDTO.setAccountNo(order.getAccountNo());
        messageDTO.setCurrency(order.getCurrency());
        messageDTO.setAmount(order.getAmount());
        messageDTO.setPaymentMethod(order.getPaymentMethod());
        messageDTO.setBankAccount(order.getBankAccount());
        messageDTO.setProof(proof);

        log.info("recharge sendMsgToReview messageDTO={}", JSONUtil.toJsonStr(messageDTO));
        SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.TRADE_EXAMINE_TOPIC, JSONUtil.toJsonStr(messageDTO));
        log.info("recharge sendMsgToReview sendResult={}", sendResult);
        if (!sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
            log.error("recharge sendMsgToReview error:{}", sendResult);
            throw new PaymentException(ExceptionCode.MESSAGE_MQ_ERROR);
        }
    }


    /**
     * 充值审核
     */
    private boolean doRechargeReview(TradeRechargeReviewCommand command) {
        String tradeNo = command.getTradeNo();

        // 校验订单是否存在
        QueryWrapper<TradeRechargeOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeRechargeOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeRechargeOrder order = tradeRechargeOrderService.getOne(orderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.RECHARGE_ORDER_NOT_EXIST, tradeNo));

        // 判断是否在审核中状态
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!TradeStatusEnum.TRADE_REVIEW.equals(tradeStatusEnum)) {
            log.error("doRechargeReview status error");
            throw new PaymentException(ExceptionCode.RECHARGE_ORDER_NOT_IN_REVIEW, tradeNo);
        }

        // 先解析TradeResult
        TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradeRechargeOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());

        // 构建审核结果
        ReviewResultDTO reviewResultDTO = new ReviewResultDTO();
        reviewResultDTO.setReviewStatus(command.isReviewStatus());
        reviewResultDTO.setReviewTime(LocalDateTime.now());
        reviewResultDTO.setReviewMsg(command.getReviewMsg());
        tradeResultDTO.setReviewResult(reviewResultDTO);

        if (!command.isReviewStatus()) {
            // 设置失败
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(command.getReviewMsg());

            // 更新订单状态
            UpdateWrapper<TradeRechargeOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(TradeRechargeOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeRechargeOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .eq(TradeRechargeOrder::getId, order.getId());
            return tradeRechargeOrderService.update(updateWrapper);
        }

        // 审核通过  处理转账资金操作
        sendMsgToRecharge(order);

        // 处理转账订单
        tradeResultDTO.setSuccess(true);
        UpdateWrapper<TradeRechargeOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(TradeRechargeOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(TradeRechargeOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .eq(TradeRechargeOrder::getId, order.getId());
        return tradeRechargeOrderService.update(updateWrapper);
    }


    /**
     * 审核成功，提交清结算操作
     */
    private void sendMsgToRecharge(TradeRechargeOrder order) {
        String tradeNo = order.getTradeNo();
        RechargeMqMessageDTO messageDTO = new RechargeMqMessageDTO();
        messageDTO.setBusinessNo(order.getBusinessNo());
        messageDTO.setTradeNo(tradeNo);
//        messageDTO.setTradeTime(order.getTradeTime().format(TradeConstant.DF_0));
        messageDTO.setMerchantId(order.getMerchantId());
        messageDTO.setMerchantName(order.getMerchantName());
        messageDTO.setAccountNo(order.getAccountNo());
        messageDTO.setCurrency(order.getCurrency());
        messageDTO.setAmount(order.getAmount());

        try {
            SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.RECHARGE_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("sendMsgToRecharge tradeNo={} sendResult={}", tradeNo, sendResult);
        } catch (Exception e) {
            log.error("recharge handler review exception:", e);

            // 并设置失败
            String error = StringUtils.isNoneBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;
            TradeResultDTO tradeResultDTO = new TradeResultDTO();
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(error);
            UpdateWrapper<TradeRechargeOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(TradeRechargeOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeRechargeOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .eq(TradeRechargeOrder::getId, order.getId());
            tradeRechargeOrderService.update(updateWrapper);
            throw new PaymentException(ExceptionCode.RECHARGE_CHANNEL_ERROR, tradeNo, error);
        }

    }

    /**
     * 发起充值审核
     */
    private boolean doReviewRecharge(String tradeNo) {
        QueryWrapper<TradeRechargeOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeRechargeOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeRechargeOrder order = tradeRechargeOrderService.getOne(orderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.RECHARGE_ORDER_NOT_EXIST, tradeNo));

        // 已经成功/审核中, 无需再次审核
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (Arrays.asList(TradeStatusEnum.TRADE_SUCCESS, TradeStatusEnum.TRADE_REVIEW).contains(tradeStatusEnum)) {
            log.error("doReviewRecharge order already success|review status, tradeNo={}", tradeNo);
            throw new PaymentException(ExceptionCode.RECHARGE_ALREADY_SUCCESS, tradeNo);
        }

        // 凭证截图等
        String proof = Optional.of(order).map(TradeRechargeOrder::getAttribute)
                .map(e -> JSONUtil.toBean(e, TradeRechargeAttributeDTO.class))
                .map(TradeRechargeAttributeDTO::getProof)
                .orElse(null);

        // 发起审核
        sendMsgToReview(order, proof);

        UpdateWrapper<TradeRechargeOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(TradeRechargeOrder::getTradeStatus, TradeStatusEnum.TRADE_REVIEW.getCode())
                .eq(TradeRechargeOrder::getId, order.getId());
        return tradeRechargeOrderService.update(updateWrapper);
    }
}
