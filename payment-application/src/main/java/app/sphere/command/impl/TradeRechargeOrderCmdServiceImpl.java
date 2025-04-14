package app.sphere.command.impl;

import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.TradeRechargeOrderCmdService;
import app.sphere.command.cmd.*;
import app.sphere.command.dto.PreRechargeDTO;
import app.sphere.command.dto.TradeRechargeAttributeDTO;
import app.sphere.command.dto.trade.result.ReviewResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import app.sphere.manager.OrderNoManager;
import app.sphere.query.SettleAccountQueryService;
import app.sphere.query.param.SettleAccountParam;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.TradeRechargeOrderRepository;
import infrastructure.sphere.db.entity.SettleAccount;
import infrastructure.sphere.db.entity.TradeRechargeOrder;
import infrastructure.sphere.remote.admin.TradeExamineParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.*;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class TradeRechargeOrderCmdServiceImpl implements TradeRechargeOrderCmdService {

    @Resource
    TradeRechargeOrderRepository tradeRechargeOrderRepository;
    @Resource
    OrderNoManager orderNoManager;
    @Resource
    SettleAccountQueryService settleAccountQueryService;
    @Resource
    SettleAccountCmdService settleAccountCmdService;


    @Override
    public PreRechargeDTO executePreRecharge(TradePreRechargeCommand command) {
        log.info("executePreRecharge command={}", JSONUtil.toJsonStr(command));
        return doPreRecharge(command);
    }

    @Override
    public boolean executeRecharge(TradeRechargeCommand command) {
        log.info("executePreRecharge command={}", JSONUtil.toJsonStr(command));
        return doRecharge(command);
    }


    @Override
    public void executeRechargeReview(TradeRechargeReviewCommand command) {
        log.info("executeRechargeReview command={}", JSONUtil.toJsonStr(command));
        doRechargeReview(command);
    }


    @Override
    public boolean reviewRecharge(String tradeNo) {
        log.info("reviewRecharge tradeNo={}", tradeNo);
        return doReviewRecharge(tradeNo);
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
        String bankAccountName;
        String paymentMethod = command.getPaymentMethod();
        if (StringUtils.equals(paymentMethod, TradeConstant.RECHARGE_BNC)) {
            bankAccount = TradeConstant.RECHARGE_BNC_ACCOUNT;
            bankAccountName = TradeConstant.RECHARGE_BNC_HOLDER_NAME;
        } else {
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, paymentMethod);
        }

        // 校验订单 保存订单
        TradeRechargeOrder order = saveTradeRechargeOrder(command, randomAmount, bankAccount);

        // 返回
        PreRechargeDTO dto = new PreRechargeDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setPaymentMethod(paymentMethod);
        dto.setBankAccount(order.getBankAccount());
        dto.setBankAccountName(bankAccountName);

        dto.setRechargeCurrency(command.getRechargeCurrency());
        dto.setRechargeAmount(command.getRechargeAmount());
        dto.setRandomAmount(randomAmount);

        dto.setCurrency(order.getCurrency());
        dto.setAmount(order.getAmount());
        return dto;
    }

    /**
     * 充值操作
     */
    private boolean doRecharge(TradeRechargeCommand command) {
        // 如果充值成功,请提供证明
        if (command.isStatus() && StringUtils.isBlank(command.getProof())) {
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, "Recharge proof");
        }

        // 校验订单
        String tradeNo = command.getTradeNo();
        QueryWrapper<TradeRechargeOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeRechargeOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeRechargeOrder order = tradeRechargeOrderRepository.getOne(orderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo));

        // 校验状态必须是初始化
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!TradeStatusEnum.TRADE_INIT.equals(tradeStatusEnum)) {
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, command.getTradeNo());
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
            return tradeRechargeOrderRepository.update(updateWrapper);
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
        return tradeRechargeOrderRepository.update(updateWrapper);
    }

    /**
     * 校验订单 保存订单
     */
    private TradeRechargeOrder saveTradeRechargeOrder(TradePreRechargeCommand command,
                                                      BigDecimal randomAmount,
                                                      String bankAccount) {
        //校验商户 地区、账户、
        SettleAccountParam settleAccountParam = new SettleAccountParam();
        settleAccountParam.setMerchantId(command.getMerchantId());
        settleAccountParam.setAccountNo(command.getAccountNo());
        SettleAccount settleAccount = settleAccountQueryService.getSettleAccount(settleAccountParam);

        String tradeNo = orderNoManager.getTradeNo(settleAccount.getRegion(), TradeTypeEnum.RECHARGE, command.getMerchantId());
        TradeRechargeOrder order = new TradeRechargeOrder();
        order.setTradeNo(tradeNo);
        order.setPurpose(command.getPurpose());

        order.setMerchantId(command.getMerchantId());
        order.setMerchantName(command.getMerchantName());
        order.setAccountNo(command.getAccountNo());

        // 金额 商户手续费 商户分润 到账金额 通道成本 平台利润
        BigDecimal exchangeRate = BigDecimal.ONE;
        order.setRechargeCurrency(command.getRechargeCurrency());
        order.setRechargeAmount(command.getRechargeAmount().add(randomAmount));
        order.setCurrency(settleAccount.getCurrency());
        order.setAmount(order.getRechargeAmount().multiply(exchangeRate));
        order.setMerchantFee(BigDecimal.ZERO);
        order.setMerchantProfit(BigDecimal.ZERO);
        order.setAccountAmount(order.getAmount());
        order.setChannelCost(BigDecimal.ZERO);
        order.setPlatformProfit(BigDecimal.ZERO);

        order.setPaymentMethod(command.getPaymentMethod());
        order.setBankAccount(bankAccount);
        order.setTradeStatus(TradeStatusEnum.TRADE_INIT.getCode());
        order.setTradeTime(System.currentTimeMillis());
        order.setSettleStatus(SettleStatusEnum.SETTLE_TODO.getCode());
        order.setCreateTime(LocalDateTime.now());
        boolean save = tradeRechargeOrderRepository.save(order);
        log.info("saveTradeRechargeOrder save={}", save);
        return order;
    }

    /**
     * 发起审核
     */
    private void sendMsgToReview(TradeRechargeOrder order, String proof) {
        TradeExamineParam examineParam = new TradeExamineParam();
        examineParam.setTradeNo(order.getTradeNo());
        examineParam.setMerchantId(order.getMerchantId());
        examineParam.setMerchantName(order.getMerchantName());
        examineParam.setProductDetail(order.getPurpose());
        examineParam.setItemDetailInfo(order.getPurpose());
        examineParam.setCurrency(order.getCurrency());
        examineParam.setAmount(order.getAmount().toString());
        examineParam.setPaymentMethod(order.getPaymentMethod());
        examineParam.setCashAccount(order.getBankAccount());
        examineParam.setRemark(proof);
    }


    /**
     * 充值审核
     */
    private boolean doRechargeReview(TradeRechargeReviewCommand command) {
        String tradeNo = command.getTradeNo();

        // 校验订单是否存在
        QueryWrapper<TradeRechargeOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeRechargeOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeRechargeOrder order = tradeRechargeOrderRepository.getOne(orderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo));

        // 判断是否在审核中状态
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!TradeStatusEnum.TRADE_REVIEW.equals(tradeStatusEnum)) {
            log.error("doRechargeReview status error");
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo);
        }

        // 先解析TradeResult
        TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradeRechargeOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());

        // 构建审核结果
        ReviewResultDTO reviewResultDTO = new ReviewResultDTO();
        reviewResultDTO.setReviewStatus(command.isReviewStatus());
        reviewResultDTO.setReviewTime(System.currentTimeMillis());
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
            return tradeRechargeOrderRepository.update(updateWrapper);
        }

        // 审核通过  处理转账资金操作
        //sendMsgToRecharge(order);
        SettleAccountRechargeCommand rechargeCommand = new SettleAccountRechargeCommand();
        BeanUtils.copyProperties(order, rechargeCommand);
        settleAccountCmdService.handlerAccountRecharge(rechargeCommand);

        // 处理转账订单
        tradeResultDTO.setSuccess(true);
        UpdateWrapper<TradeRechargeOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(TradeRechargeOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(TradeRechargeOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .eq(TradeRechargeOrder::getId, order.getId());
        return tradeRechargeOrderRepository.update(updateWrapper);
    }

    /**
     * 发起充值审核
     */
    private boolean doReviewRecharge(String tradeNo) {
        QueryWrapper<TradeRechargeOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeRechargeOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeRechargeOrder order = tradeRechargeOrderRepository.getOne(orderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo));

        // 已经成功/审核中, 无需再次审核
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (Arrays.asList(TradeStatusEnum.TRADE_SUCCESS, TradeStatusEnum.TRADE_REVIEW).contains(tradeStatusEnum)) {
            log.error("doReviewRecharge order already success|review status, tradeNo={}", tradeNo);
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo);
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
        return tradeRechargeOrderRepository.update(updateWrapper);
    }
}
