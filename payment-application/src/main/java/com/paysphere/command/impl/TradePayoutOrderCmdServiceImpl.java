package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.TradeCallBackCmdService;
import com.paysphere.command.TradePayoutOrderCmdService;
import com.paysphere.command.cmd.MerchantCommand;
import com.paysphere.command.cmd.MoneyCommand;
import com.paysphere.command.cmd.TradeCashCommand;
import com.paysphere.command.cmd.TradeCashRefundCommand;
import com.paysphere.command.cmd.TradeCashReviewCommand;
import com.paysphere.command.cmd.TradeCashSupplementCommand;
import com.paysphere.command.cmd.TradeCommand;
import com.paysphere.command.dto.PaymentResultAttributeDTO;
import com.paysphere.command.dto.TradeMerchantDTO;
import com.paysphere.command.dto.TradeMoneyDTO;
import com.paysphere.command.dto.TradePayoutChannelDTO;
import com.paysphere.command.dto.TradePayoutDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import com.paysphere.command.dto.trade.result.MerchantResultDTO;
import com.paysphere.command.dto.trade.result.PaymentResultDTO;
import com.paysphere.command.dto.trade.result.ReviewResultDTO;
import com.paysphere.command.dto.trade.result.TradeResultDTO;
import com.paysphere.config.TradeKeyConfig;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.db.entity.MerchantPaymentChannelConfig;
import com.paysphere.db.entity.MerchantPayoutChannelConfig;
import com.paysphere.db.entity.MerchantPayoutConfig;
import com.paysphere.db.entity.TradePayoutOrder;
import com.paysphere.enums.CallBackStatusEnum;
import com.paysphere.enums.DeductionTypeEnum;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.TradeCashSourceEnum;
import com.paysphere.enums.TradeModeEnum;
import com.paysphere.enums.TradeOrderOptType;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.manager.MerchantManager;
import com.paysphere.manager.OrderNoManager;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.mq.dto.examine.TradeCashExamineMqMessageDTO;
import com.paysphere.mq.dto.settle.UnfrozenMessageDTO;
import com.paysphere.query.dto.MerchantTradeDTO;
import com.paysphere.repository.TradePayoutOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class TradePayoutOrderCmdServiceImpl implements TradePayoutOrderCmdService {

    @Resource
    TradePayoutOrderService tradePayoutOrderService;

    @Resource
    RedisService redisService;
    @Resource
    RocketMqProducer rocketMqProducer;
    @Resource
    TradeKeyConfig tradeKeyConfig;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    TradeCallBackCmdService tradeCallBackCmdService;
    @Resource
    MerchantManager merchantManager;
    @Resource
    OrderNoManager orderNoManager;

    @Override
    public TradePayoutDTO executeCash(TradeCashCommand command) {
        log.info("trade executeCash command={}", JSONUtil.toJsonStr(command));
        return redisService.lock(TradeConstant.LOCK_PREFIX_CASH + command.getOrderNo(),
                () -> doCash(command));
    }

    @Override
    public void executeCashReview(TradeCashReviewCommand command) {
        log.info("trade cashReview command={}", JSONUtil.toJsonStr(command));
        redisService.lock(TradeConstant.LOCK_PREFIX_CASH_REVIEW + command.getTradeNo(),
                () -> doCashReview(command));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executeCashSupplement(TradeCashSupplementCommand command) {
        log.info("trade cashSupplement command={}", JSONUtil.toJsonStr(command));
        return redisService.lock(TradeConstant.LOCK_PREFIX_CASH_SUPPLEMENT + command.getTradeNo(),
                () -> doCashSupplement(command));
    }


    @Override
    public boolean executeCashRefund(TradeCashRefundCommand command) {
        log.info("trade cashRefund command={}", JSONUtil.toJsonStr(command));
        return redisService.lock(TradeConstant.LOCK_PREFIX_CASH_REFUND + command.getTradeNo(),
                () -> doCashRefund(command));
    }


    // ---------------------------------------------------------------------------------------------------------

    /**
     * 代付
     */
    private TradePayoutDTO doCash(TradeCashCommand command) {
        // 校验校验订单
        verifyOrder(command);

        // 校验商户、如果传入回调地址，则替代
        MerchantTradeDTO merchantDTO = verifyMerchant(command);
        if (StringUtils.isNotBlank(command.getCallbackUrl())) {
            merchantDTO.getMerchantConfig().setFinishCashUrl(command.getCallbackUrl());
        }

        // 校验金额、下单入库
        TradePayoutOrder order = saveTradeCashOrder(command, merchantDTO);

        // 执行冻结操作, 同步
        cashFrozenAmount(order);

        // 判断是否需要人工审核
        if (configCashReview(merchantDTO.getMerchantPayoutConfig(), order)) {
            return pendingReview(order);
        }

        // 异步执行代付
        threadPoolTaskExecutor.execute(() -> aycExecuteCash(order, merchantDTO));
        return buildCashDTO(order);
    }

    /**
     * 返回代付数据
     */
    private TradePayoutDTO buildCashDTO(TradePayoutOrder order) {
        TradePayoutDTO dto = new TradePayoutDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setOuterNo(order.getOuterNo());
//        dto.setStatus(PaymentStatusEnum.PAYMENT_PROCESSING.getMerchantStatus());
        dto.setDisbursementTime(getDisbursementTime(null)); // 当前时间

        TradeMerchantDTO merchantDTO = new TradeMerchantDTO();
        merchantDTO.setMerchantId(order.getMerchantId());
        merchantDTO.setMerchantName(order.getMerchantName());
        merchantDTO.setAccountNo(order.getAccountNo());
        dto.setMerchant(merchantDTO);

        TradePayoutChannelDTO channelDTO = new TradePayoutChannelDTO();
        channelDTO.setCashAccount(order.getCashAccount());
        channelDTO.setPaymentMethod(order.getPaymentMethod());
        dto.setChannel(channelDTO);

        TradeMoneyDTO moneyDTO = new TradeMoneyDTO();
        moneyDTO.setCurrency(order.getCurrency());
        moneyDTO.setAmount(order.getAmount());
        dto.setMoney(moneyDTO);

        return dto;
    }

    /**
     * 异步执行代付操作
     */
    private void aycExecuteCash(TradePayoutOrder order, MerchantTradeDTO merchantDTO) {
        try {
            // 发起代付支付
//            DisbursementDTO disbursementDTO = cashPostPayment(order, merchantDTO);

            // 处理支付成功
//            handlerSuccessTradeResult(disbursementDTO, order);

            // 校验代付平台利润
//            verifyCashPlatformProfit(order);
        } catch (Exception e) {
            log.error("aycExecuteCash tradeNo={} doCash exception", order.getTradeNo(), e);

            // 超时异常不做处理
            String errorMsg = e.getMessage();
            if (StringUtils.isNotBlank(errorMsg) && (errorMsg.contains(TradeConstant.SOCKET_TIME_OUT) || errorMsg.contains(TradeConstant.SOCKET_UNKNOWN))) {
                String exMsg = "Paysphere Payout"
                        + "\nTradeNo: " + order.getTradeNo()
                        + "\nOuterNo: " + order.getOuterNo()
                        + "\nSocket exception: " + errorMsg
                        + "\nPlease check";
                rocketMqProducer.syncSendExceptionMessage(exMsg);
                return ;
            }

            // 异常解冻资金
            cashUnfrozenAmount(order);

            // 更新订单状态
            errorMsg = StringUtils.isNotBlank(errorMsg) ? errorMsg : TradeConstant.ERROR_TO_CHECK;
            handlerFailedTradeResult(order, errorMsg);

            // 美化异常消息
            log.error("paysphere trade doCash {} exception errorMsg={}", order.getOuterNo(), errorMsg);

            // 回调或者抛异常
            Optional<MerchantResultDTO> merchantResultDTO = Optional.of(order).map(TradePayoutOrder::getTradeResult)
                    .map(re -> JSONUtil.toBean(re, TradeResultDTO.class))
                    .map(TradeResultDTO::getMerchantResult);
//            LocalDateTime tradeLocalTime = Optional.of(order).map(TradePayoutOrder::getTradeTime).orElse(LocalDateTime.now());
            TradeCashSourceEnum sourceEnum = TradeCashSourceEnum.codeToEnum(order.getSource());

            // 解析回调地址
            String finishCashUrl = merchantResultDTO.map(MerchantResultDTO::getFinishCashUrl).orElse(null);
            TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
            bodyDTO.setTradeNo(order.getTradeNo());
            bodyDTO.setOrderNo(order.getOuterNo());
            bodyDTO.setMerchantId(order.getMerchantId());
            bodyDTO.setMerchantName(order.getMerchantName());
            bodyDTO.setPaymentMethod(order.getPaymentMethod());
            bodyDTO.setStatus(TradeStatusEnum.TRADE_FAILED.getMerchantStatus());
//            bodyDTO.setTransactionTime(String.valueOf(tradeLocalTime));

            TradeCallBackMoneyDTO moneyDTO = new TradeCallBackMoneyDTO();
            moneyDTO.setCurrency(order.getCurrency());
            moneyDTO.setAmount(order.getAmount());
            bodyDTO.setMoney(moneyDTO);

            TradeCallBackDTO callBackDTO = new TradeCallBackDTO();
            callBackDTO.setMode(TradeModeEnum.PRODUCTION.getMode());
            callBackDTO.setSource(sourceEnum.name());
            callBackDTO.setUrl(finishCashUrl);
            callBackDTO.setBody(bodyDTO);
            tradeCallBackCmdService.handlerTradeCallback(callBackDTO);
        }
    }


    /**
     * 冻结商户资金账户
     */
    private void cashFrozenAmount(TradePayoutOrder order) {
        String tradeNo = order.getTradeNo();
        BigDecimal actualAmount = order.getActualAmount();

       /* AccountAmountFrozenParam frozenParam = new AccountAmountFrozenParam();
        frozenParam.setBusinessNo(order.getBusinessNo());
        frozenParam.setTradeNo(tradeNo);
        frozenParam.setOuterNo(order.getOuterNo());
        frozenParam.setCurrency(order.getCurrency());
        frozenParam.setAmount(actualAmount); // 实扣金额，非交易金额
        frozenParam.setMerchantId(order.getMerchantId());
        frozenParam.setMerchantName(order.getMerchantName());
        frozenParam.setAccountNo(order.getAccountNo());

        log.info("cashFrozenAmount param={}", JSONUtil.toJsonStr(frozenParam));
        try {
            Mono<Result<Boolean>> resultMono = settleApiService.frozenAmount(frozenParam);
            Result<Boolean> result = resultMono.toFuture().join();
            Boolean frozen = BaseResult.parse(result);
            log.info("cashFrozenAmount result={}", frozen);
        } catch (Exception e) {
            log.error("cashFrozenAmount tradeNo:{}. failed to frozen amount exception", order.getOuterNo(), e);

            // 更新订单状态
            String error = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "Failed to frozen amount";
            handlerFailedTradeResult(order, error);
            String frozenMsg = order.getCurrency() + " " + order.getActualAmount().setScale(0, RoundingMode.UP);
            throw new PaymentException(ExceptionCode.CASH_FROZEN_AMOUNT_FAILED, order.getOuterNo(), frozenMsg);
        }*/
    }

    /**
     * 解冻商户资金账户
     */
    private void cashUnfrozenAmount(TradePayoutOrder order) {
        String tradeNo = order.getTradeNo();
        UnfrozenMessageDTO unfrozenMessageDTO = new UnfrozenMessageDTO();
        unfrozenMessageDTO.setTradeNo(tradeNo);
        unfrozenMessageDTO.setOuterNo(order.getOuterNo());

        log.info("cashUnfrozenAmount unfrozenMessageDTO={}", JSONUtil.toJsonStr(unfrozenMessageDTO));
        SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.UNFROZEN_TOPIC, JSONUtil.toJsonStr(unfrozenMessageDTO));
        log.info("cashUnfrozenAmount tradeNo={}, result={}", tradeNo, sendResult);
    }


    /**
     * 校验订单是否已存在
     */
    private void verifyOrder(TradeCashCommand command) {
        String outerNo = command.getOrderNo();

        QueryWrapper<TradePayoutOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.select("outer_no as outerNo");
        payOrderQuery.lambda().eq(TradePayoutOrder::getOuterNo, outerNo).last(TradeConstant.LIMIT_1);
        TradePayoutOrder order = tradePayoutOrderService.getOne(payOrderQuery);
        Assert.isNull(order, () -> new PaymentException(ExceptionCode.CASH_ORDER_REPEAT, outerNo));
    }


    /**
     * 代付审核后处理
     */
    private boolean doCashReview(TradeCashReviewCommand command) {
        String tradeNo = command.getTradeNo();

        // 校验订单是否存在
        QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePayoutOrder order = tradePayoutOrderService.getOne(cashOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.CASH_ORDER_NOT_EXIST, tradeNo));

        // 判断是否在审核中状态
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!TradeStatusEnum.TRADE_REVIEW.equals(tradeStatusEnum)) {
            throw new PaymentException(ExceptionCode.CASH_ORDER_NOT_IN_REVIEW, tradeNo);
        }

        // 先解析TradeResult
        TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());

        // 构建审核结果
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

            // 解冻资金, 此处如果异常，完了，状态一直在审核中，冻结的金额也没有解冻，但至少有消息，该如何手动解决呢？ 不知道
            cashUnfrozenAmount(order);

            // 更新订单状态
            UpdateWrapper<TradePayoutOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(TradePayoutOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradePayoutOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradePayoutOrder::getPaymentFinishTime, LocalDateTime.now())
                    .set(TradePayoutOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .eq(TradePayoutOrder::getId, order.getId());
            return tradePayoutOrderService.update(updateWrapper);
        }

        try {
            // 审核通过 发起代付
            PaymentResultDTO paymentResult = tradeResultDTO.getPaymentResult();
            /*MerchantCashPaymentConfigDTO cashPaymentConfigDTO = new MerchantCashPaymentConfigDTO();
            cashPaymentConfigDTO.setPaymentMethod(paymentResult.getPaymentMethod());
            cashPaymentConfigDTO.setChannelCode(paymentResult.getChannelCode());
            cashPaymentConfigDTO.setChannelName(paymentResult.getChannelName());
            MerchantDTO merchantDTO = new MerchantDTO();
            merchantDTO.setMerchantCashPaymentConfig(cashPaymentConfigDTO);

            DisbursementDTO disbursementDTO = cashPostPayment(order, merchantDTO);
            handlerSuccessTradeResult(disbursementDTO, order);*/
            return true;
        } catch (Exception e) {
            log.error("doCashReview tradeNo={} exception:", command.getTradeNo(), e);

            // 异常解冻资金
            cashUnfrozenAmount(order);

            // 更新订单状态
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;
            handlerFailedTradeResult(order, errorMsg);

            // 美化异常消息
            log.error("doCashReview tradeNo={} exception errorMsg={}", command.getTradeNo(), errorMsg);
            throw new PaymentException(ExceptionCode.CASH_CHANNEL_ERROR, order.getOuterNo(), errorMsg);
        }
    }


    /**
     * 校验并得到商户代付配置
     */
    private MerchantTradeDTO verifyMerchant(TradeCashCommand command) {
        String merchantId = command.getMerchant().getMerchantId();
        String paymentMethod = command.getPaymentMethod();
        BigDecimal amount = command.getMoney().getAmount();
        MerchantTradeDTO merchantDTO = merchantManager.getMerchantDTO(merchantId, TradeTypeEnum.PAYOUT, paymentMethod, amount);

        // 校验配置
        MerchantPayoutConfig merchantPayoutConfig = merchantDTO.getMerchantPayoutConfig();
        if (Objects.isNull(merchantPayoutConfig)) {
            log.error("verifyMerchant merchantCashConfig is null. command={}", JSONUtil.toJsonStr(command));
            throw new PaymentException(ExceptionCode.CASH_CONFIG_NOT_EXIST, merchantId);
        }

        // 校验渠道配置
        MerchantPaymentChannelConfig merchantPaymentChannelConfig = merchantDTO.getMerchantPaymentChannelConfig();
        if (Objects.isNull(merchantPaymentChannelConfig)) {
            log.error("verifyMerchant paymentCashConfig is null. command={}", JSONUtil.toJsonStr(command));
            throw new PaymentException(ExceptionCode.CASH_CHANNEL_CONFIG_EMPTY, merchantId);
        }
        
        return merchantDTO;
    }


    /**
     * 代付下单, 此步骤保存了商户的配置信息到tradeResult
     */
    private TradePayoutOrder saveTradeCashOrder(TradeCashCommand command, MerchantTradeDTO merchantDTO) {
        Merchant merchant = merchantDTO.getMerchant();

        // 渠道信息 FIX channelName不要超过10
        // 商户名称 FIX 取值BrandName
        String merchantName = Optional.of(command).map(TradeCommand::getMerchant)
                .map(MerchantCommand::getMerchantName)
                .orElse(merchant.getBrandName());
        // 账户号-存在
        String accountNo = null; //getAccountNo(merchantDTO);
        log.info("saveTradeCashOrder  merchantName={}, accountNo={}",  merchantName, accountNo);

        // 构建代付提前交易结果
        MoneyCommand money = command.getMoney();
        TradeResultDTO tradeResultDTO = getTradeResultDTO(merchantDTO, money.getAmount());
        MerchantResultDTO merchantResult = tradeResultDTO.getMerchantResult();
        log.info("saveTradeCashOrder tradeResultDTO={}", JSONUtil.toJsonStr(tradeResultDTO));

        // 构建金额, 如果内扣，那么减去手续费可能会导致到账金额小于0，则抛出异常
        BigDecimal accountAmount = merchantResult.getAccountAmount();
        if (accountAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.error("saveTradeCashOrder accountAmount is less than zero. accountAmount={}", accountAmount);
            throw new PaymentException(ExceptionCode.CASH_ACTUAL_AMOUNT_ERROR,
                    accountAmount.setScale(0, RoundingMode.UP).toString());
        }

        // 构建代付订单
        String outerNo = command.getOrderNo();
        String tradeNo = orderNoManager.getTradeNo(null, TradeTypeEnum.PAYOUT, merchant.getMerchantId());

        TradePayoutOrder order = new TradePayoutOrder();
        order.setBusinessNo(orderNoManager.getBusinessNo());
        order.setTradeNo(tradeNo);
        order.setOuterNo(outerNo);
        order.setPurpose(command.getPurpose());
        order.setProductDetail(command.getProductDetail());
        order.setPaymentMethod(command.getPaymentMethod());
        order.setChannelCode(null);
        order.setCashAccount(command.getCashAccount());
        order.setMerchantId(command.getMerchant().getMerchantId());
        order.setMerchantName(merchantName);
        order.setAccountNo(accountNo);

        // 金额 实扣金额 商户分润 商户手续费 商户到账金额
        order.setCurrency(money.getCurrency());
        order.setAmount(money.getAmount());
        order.setActualAmount(merchantResult.getActualAmount());
        order.setMerchantFee(merchantResult.getMerchantFee());
        order.setMerchantProfit(merchantResult.getMerchantProfit());
        order.setAccountAmount(merchantResult.getAccountAmount());

        order.setPayerInfo(JSONUtil.toJsonStr(command.getPayer()));
        order.setReceiverInfo(JSONUtil.toJsonStr(command.getReceiver()));
//        order.setTradeTime(LocalDateTime.now());
        order.setTradeStatus(TradeStatusEnum.TRADE_INIT.getCode());
        order.setTradeResult(JSONUtil.toJsonStr(tradeResultDTO));
//        order.setPaymentStatus(PaymentStatusEnum.PAYMENT_UNPAID.getCode());

        order.setSettleStatus(SettleStatusEnum.SETTLE_TODO.getCode());
        order.setCallBackStatus(CallBackStatusEnum.CALLBACK_TODO.getCode());
        order.setSource(command.getTradeCashSource().getCode());
        order.setVersion(TradeConstant.INIT_VERSION);
        order.setArea(merchant.getArea());
        order.setAttribute("{}");
//        order.setIp(IpManager.getIpAddress());
        order.setCreateTime(LocalDateTime.now());
        tradePayoutOrderService.save(order);
        return order;
    }

    /**
     * 解析支付前的交易结果
     */
    private TradeResultDTO getTradeResultDTO(MerchantTradeDTO merchantDTO, BigDecimal amount) {
        TradeResultDTO tradeResultDTO = new TradeResultDTO();

        // 请求前，回调地址、扣款方式已知
        Merchant merchant = merchantDTO.getMerchant();
        MerchantConfig merchantConfig = merchantDTO.getMerchantConfig();
        MerchantPayoutConfig merchantPayoutConfig = merchantDTO.getMerchantPayoutConfig();
        MerchantPayoutChannelConfig merchantPayoutChannelConfig = merchantDTO.getMerchantPayoutChannelConfig();

        BigDecimal singleFee = merchantPayoutChannelConfig.getSingleFee();
        BigDecimal singleRate = merchantPayoutChannelConfig.getSingleRate();
        Integer deductionType = merchantPayoutConfig.getDeductionType();

        // 商户结果
        MerchantResultDTO merchantResult = new MerchantResultDTO();
        merchantResult.setMerchantId(merchant.getMerchantId());
        merchantResult.setMerchantName(merchant.getMerchantName());
        merchantResult.setAccountNo(null);
        merchantResult.setAccountName(null);
        merchantResult.setFinishCashUrl(merchantConfig.getFinishCashUrl());
        merchantResult.setDeductionType(deductionType);
        merchantResult.setSingleFee(singleFee);
        merchantResult.setSingleRate(singleRate);
        merchantResult.setSettleType(merchantPayoutChannelConfig.getSettleType());
        merchantResult.setSettleTime(merchantPayoutChannelConfig.getSettleTime());

        // 商户手续费 FIX *
        BigDecimal merchantFee = amount.multiply(singleRate).add(singleFee);
        DeductionTypeEnum deductionTypeEnum = DeductionTypeEnum.codeToEnum(deductionType);

        BigDecimal actualAmount;
        BigDecimal accountAmount;
        if (DeductionTypeEnum.DEDUCTION_INTERNAL.equals(deductionTypeEnum)) {
            actualAmount = amount;
            accountAmount = amount.subtract(merchantFee);
        } else if (DeductionTypeEnum.DEDUCTION_EXTERNAL.equals(deductionTypeEnum)) {
            actualAmount = amount.add(merchantFee);
            accountAmount = amount;
        } else {
            throw new PaymentException(ExceptionCode.UNSUPPORTED_DEDUCTION_TYPE);
        }
        merchantResult.setActualAmount(actualAmount);
        merchantResult.setMerchantFee(merchantFee);
        merchantResult.setAccountAmount(accountAmount);


        // 对于代付， 支付方式也已知, 但支付方式的费率未知
        PaymentResultDTO paymentResult = new PaymentResultDTO();
        paymentResult.setChannelCode(merchantPayoutChannelConfig.getChannelCode());
        paymentResult.setChannelName(merchantPayoutChannelConfig.getChannelName());
        paymentResult.setPaymentMethod(merchantPayoutChannelConfig.getPaymentMethod());

        tradeResultDTO.setMerchantResult(merchantResult);
        tradeResultDTO.setPaymentResult(paymentResult);
        return tradeResultDTO;
    }


    /**
     * 满足人工审核条件
     */
    private boolean configCashReview(MerchantPayoutConfig merchantCashConfig, TradePayoutOrder order) {
        // 通过开关和金额判断, 如果开关开启 且金额大于设置金额
        Boolean cashReview = Optional.of(merchantCashConfig)
                .map(MerchantPayoutConfig::isCashReview)
                .orElse(Boolean.TRUE);

        // 审核开关 且 金额大于审核金额
        return cashReview;
    }


    /**
     * 发送消息到admin。等待审核
     */
    private TradePayoutDTO pendingReview(TradePayoutOrder order) {
        // 构建审核参数
        TradeCashExamineMqMessageDTO messageDTO = new TradeCashExamineMqMessageDTO();
        messageDTO.setBusinessNo(order.getBusinessNo());
        messageDTO.setTradeNo(order.getTradeNo());
        messageDTO.setOuterNo(order.getOuterNo());
        messageDTO.setMerchantId(order.getMerchantId());
        messageDTO.setMerchantName(order.getMerchantName());
        messageDTO.setProductDetail(order.getProductDetail());
//        messageDTO.setItemDetailInfo(order.getItemDetailInfo());
        messageDTO.setPaymentMethod(order.getPaymentMethod());
        messageDTO.setCashAccount(order.getCashAccount());
        messageDTO.setCurrency(order.getCurrency());
        messageDTO.setAmount(order.getAmount());
        messageDTO.setPayerInfo(order.getPayerInfo());
        messageDTO.setReceiverInfo(order.getReceiverInfo());
        messageDTO.setApplyOperator(order.getMerchantName());
        messageDTO.setRemark(order.getAttribute());

        // 发送消息去审核
        String message = JSONUtil.toJsonStr(messageDTO);
        log.info("pendingReview messageDTO={}", JSONUtil.toJsonStr(messageDTO));
        SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.TRADE_EXAMINE_TOPIC, message);
        log.info("pendingReview sendResult={}", sendResult);

        // 更新订单审核中
        order.setTradeStatus(TradeStatusEnum.TRADE_REVIEW.getCode());
        UpdateWrapper<TradePayoutOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(TradePayoutOrder::getTradeStatus, order.getTradeStatus())
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePayoutOrder::getId, order.getId());
        tradePayoutOrderService.update(updateWrapper);

//        LocalDateTime tradeTime = Optional.of(order).map(TradePayoutOrder::getTradeTime).orElse(LocalDateTime.now());
//        String disbursementTime = ZonedDateTime.of(tradeTime, TradeConstant.ZONE_ID).format(TradeConstant.DF_3);

        // 构建返回数据
        TradePayoutDTO dto = new TradePayoutDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setStatus(TradeStatusEnum.codeToEnum(order.getTradeStatus()).getMerchantStatus());
//        dto.setDisbursementTime(disbursementTime);
        dto.setOuterNo(order.getOuterNo());

        TradeMerchantDTO merchantDTO = new TradeMerchantDTO();
        merchantDTO.setMerchantId(order.getMerchantId());
        merchantDTO.setMerchantName(order.getMerchantName());
        merchantDTO.setAccountNo(order.getAccountNo());
        dto.setMerchant(merchantDTO);

        TradeMoneyDTO tradeMoneyDTO = new TradeMoneyDTO();
        tradeMoneyDTO.setCurrency(order.getCurrency());
        tradeMoneyDTO.setAmount(order.getAmount());
        dto.setMoney(tradeMoneyDTO);

        TradePayoutChannelDTO cashChannelDTO = new TradePayoutChannelDTO();
        cashChannelDTO.setPaymentMethod(order.getPaymentMethod());
        cashChannelDTO.setCashAccount(order.getCashAccount());
        cashChannelDTO.setAccountName(order.getMerchantName());
        dto.setChannel(cashChannelDTO);
        return dto;
    }

    /**
     * 更新错误状态\结果
     */
    private void handlerFailedTradeResult(TradePayoutOrder order, String error) {
        log.info("handlerFailedTradeResult orderNo={}, error={}", order.getTradeNo(), error);

        // 解析交易前预存的结果
        TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());

        // 结果
        tradeResultDTO.setSuccess(false);
        tradeResultDTO.setError(error);

        // 更新交易订单：交易状态、渠道编码、交易结果
        UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
        cashOrderUpdate.lambda()
                .set(TradePayoutOrder::getTradeTime, LocalDateTime.now()) // 下单时间
                .set(TradePayoutOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                .set(TradePayoutOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                .set(TradePayoutOrder::getPaymentFinishTime, LocalDateTime.now())
                .set(TradePayoutOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePayoutOrder::getId, order.getId());
        tradePayoutOrderService.update(cashOrderUpdate);
    }

    /**
     * 支付返回，同步插入
     */
    private void handlerSuccessTradeResult(TradePayoutOrder order) {
        log.info("handlerSuccessTradeResult orderNo={}", order.getTradeNo());

        // 解析交易前预存的结果
       /* TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());
        tradeResultDTO.setSuccess(true);

        // 渠道请求结果
        DisbursementChannelDTO channelDTO = Optional.of(disbursementDTO).map(DisbursementDTO::getChannel)
                .orElse(new DisbursementChannelDTO());
        ChannelResultDTO channelResult = Optional.of(tradeResultDTO).map(TradeResultDTO::getChannelResult)
                .orElse(new ChannelResultDTO());
        channelResult.setPaymentNo(disbursementDTO.getPaymentNo());
        channelResult.setStatus(disbursementDTO.getStatus());
        channelResult.setChannelOrderNo(channelDTO.getChannelOrderNo());
        channelResult.setCashAccount(channelDTO.getDisbursementAccount());
        channelResult.setAccountName(channelDTO.getAccountName());

        // 渠道配置
        PaymentResultDTO paymentResult = Optional.of(tradeResultDTO).map(TradeResultDTO::getPaymentResult)
                .orElse(new PaymentResultDTO());
        BigDecimal singleRate = channelDTO.getSingleRate();
        BigDecimal singleFee = channelDTO.getSingleFee();
        MerchantResultDTO merchantResult = tradeResultDTO.getMerchantResult();
        BigDecimal merchantFee = merchantResult.getMerchantFee();
        BigDecimal merchantProfit = merchantResult.getMerchantProfit();

        BigDecimal channelCost = order.getAmount().multiply(singleRate).add(singleFee);
        BigDecimal platformProfit = merchantFee.subtract(merchantProfit).subtract(channelCost);
        paymentResult.setSingleFee(singleFee);
        paymentResult.setSingleRate(singleRate);
        paymentResult.setChannelCost(channelCost);
        paymentResult.setPlatformProfit(platformProfit);

        // 支付执行后, PaymentResult下单时已经设置, 现在是补充
        tradeResultDTO.setChannelResult(channelResult);
        tradeResultDTO.setPaymentResult(paymentResult);

        // tradeTime
        LocalDateTime tradeTime = Optional.of(disbursementDTO).map(DisbursementDTO::getDisbursementTime)
                .map(this::parseDateTime)
                .orElse(LocalDateTime.now());

        // 更新交易订单：交易状态、渠道编码、交易结果
        UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
        cashOrderUpdate.lambda()
                .set(TradePayoutOrder::getTradeTime, tradeTime) // 下单时间
                .set(TradePayoutOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(TradePayoutOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .set(TradePayoutOrder::getChannelCost, channelCost)
                .set(TradePayoutOrder::getPlatformProfit, platformProfit)
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePayoutOrder::getId, order.getId());
        tradePayoutOrderService.update(cashOrderUpdate);*/
    }

    /**
     * 调用支付
     */
/*    private DisbursementDTO cashPostPayment(TradePayoutOrder order, MerchantTradeDTO merchantDTO) {
        // 构建header
        HeaderParam headers = new HeaderParam();
        headers.setTradeNo(order.getTradeNo());
        headers.setSign(RSAUtils.doSign(order.getTradeNo(), tradeKeyConfig.parsePrivateKey(),
                StandardCharsets.UTF_8.name()));

        // 构建body
        DisbursementParam param = buildCashParam(order, merchantDTO);
        log.info("cashPostPayment param={}", JSONUtil.toJsonStr(param));

        Mono<Result<DisbursementDTO>> resultMono = paymentApiService.disbursement(param, BeanUtil.beanToMap(headers));
        Result<DisbursementDTO> result = resultMono.toFuture().join();
        DisbursementDTO disbursementDTO = BaseResult.parse(result);
        Assert.notNull(disbursementDTO, () -> new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "disbursementDTO is required"));
        log.info("paysphere cashPostPayment disbursementDTO={}", JSONUtil.toJsonStr(disbursementDTO));
        return disbursementDTO;
    }*/

    /**
     * 根据订单号进行代付补单
     */
    private boolean doCashSupplement(TradeCashSupplementCommand command) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePayoutOrder order = tradePayoutOrderService.getOne(cashOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.CASH_ORDER_NOT_EXIST, tradeNo));

        // 如果下单都没成功，谈何补单
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!tradeStatusEnum.equals(TradeStatusEnum.TRADE_SUCCESS)) {
            throw new PaymentException(ExceptionCode.CASH_ORDER_TRANSACTION_FAILED, tradeNo);
        }

        // 如果是支付成功状态，不必进行补单
        // 如果还未支付/支付中，能进行补单 可能原因是网络未收到回调消息等其他
        // 如果支付状态失败终态，不能进行补单
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_SUCCESS)) {
            throw new PaymentException(ExceptionCode.CASH_ORDER_PAYMENT_SUCCESS, tradeNo);
        } else if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_FAILED)) {
            throw new PaymentException(ExceptionCode.CASH_ORDER_PAYMENT_FAILED, tradeNo);
        } else {
            TradeResultDTO tradeResultDTO = Optional.of(order).map(TradePayoutOrder::getTradeResult)
                    .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                    .orElse(new TradeResultDTO());
            tradeResultDTO.setSuccess(true);

            // 结算补单
//            SupplementParam supplementParam = buildSupplementParam(order, tradeResultDTO);
//            Boolean supplement = BaseResult.parse(settleApiService.supplement(supplementParam).toFuture().join());
//            log.info("doCashSupplement tradeNo={} message: {}", tradeNo, supplement);

            PaymentResultAttributeDTO attributeDTO = new PaymentResultAttributeDTO();
            attributeDTO.setType(TradeOrderOptType.SUPPLEMENT.getName());
            attributeDTO.setOperator(command.getOperator());

            // 更新订单状态
            UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
            cashOrderUpdate.lambda()
                    .set(TradePayoutOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_SUCCESS.getCode())
                    .set(TradePayoutOrder::getPaymentFinishTime, LocalDateTime.now())
                    .set(TradePayoutOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradePayoutOrder::getAttribute, JSONUtil.toJsonStr(attributeDTO))
                    .eq(TradePayoutOrder::getId, order.getId());
            tradePayoutOrderService.update(cashOrderUpdate);
        }

        return true;
    }


    /**
     * 退单操作（强制变更状态为失败）
     */
    private boolean doCashRefund(TradeCashRefundCommand command) {
        String tradeNo = command.getTradeNo();
        String operator = command.getOperator();

        QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePayoutOrder order = tradePayoutOrderService.getOne(cashOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.CASH_ORDER_NOT_EXIST, tradeNo));

        // 如果交易都没成功，谈何退单
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!tradeStatusEnum.equals(TradeStatusEnum.TRADE_SUCCESS)) {
            throw new PaymentException(ExceptionCode.CASH_ORDER_TRANSACTION_FAILED, tradeNo);
        }

        // 如果还未支付/支付中，直接设置为失败
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
        /*if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_UNPAID)
                || paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_PROCESSING)) {
            // 更新订单状态
            PaymentResultAttributeDTO attributeDTO = new PaymentResultAttributeDTO();
            attributeDTO.setType(TradeOrderOptType.REFUND.getName());
            attributeDTO.setOperator(operator);

            TradeResultDTO tradeResultDTO = Optional.of(order).map(TradeCashOrder::getTradeResult)
                    .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                    .orElse(new TradeResultDTO());
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError("set refund operate by " + operator);

            UpdateWrapper<TradeCashOrder> cashOrderUpdate = new UpdateWrapper<>();
            cashOrderUpdate.lambda()
                    .set(TradeCashOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradeCashOrder::getPaymentFinishTime, LocalDateTime.now())
                    .set(TradeCashOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradeCashOrder::getRemark, JSONUtil.toJsonStr(attributeDTO))
                    .eq(TradeCashOrder::getId, order.getId());
            tradeCashOrderService.update(cashOrderUpdate);

            // 如果资金有冻结则需要解冻, 如果没有冻结, 解冻失败也属于正常
            UnfrozenMessageDTO unfrozenMessageDTO = new UnfrozenMessageDTO();
            unfrozenMessageDTO.setTradeNo(order.getTradeNo());
            unfrozenMessageDTO.setOuterNo(order.getOuterNo());
            log.info("doCashRefund unfrozenMessageDTO={}", JSONUtil.toJsonStr(unfrozenMessageDTO));
            SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.UNFROZEN_TOPIC, JSONUtil.toJsonStr(unfrozenMessageDTO));
            log.info("doCashRefund tradeNo={}, result={}", order.getTradeNo(), sendResult);
            if (Objects.isNull(sendResult) || !SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "doCashRefund error. tradeNo : " + order.getTradeNo());
            }
        }*/

        // 如果是支付失败状态，不必进行退单
        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_FAILED)) {
            throw new PaymentException(ExceptionCode.CASH_ORDER_PAYMENT_FAILED, tradeNo);
        }

        // 如果已经是成功状态，强制失败
        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_SUCCESS)) {
//            RefundParam refundParam = new RefundParam();
//            refundParam.setTradeNo(order.getTradeNo());
//            Boolean refund = BaseResult.parse(settleApiService.refund(refundParam).toFuture().join());
//            if (Objects.isNull(refund) || Boolean.FALSE.equals(refund)) {
//                throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "refund error. tradeNo : " + tradeNo);
//            }

            // 更新订单状态
            TradeResultDTO tradeResultDTO = Optional.of(order).map(TradePayoutOrder::getTradeResult)
                    .map(e -> JSONUtil.toBean(e, TradeResultDTO.class)).orElse(new TradeResultDTO());
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError("set refund operate by " + operator);

            PaymentResultAttributeDTO attributeDTO = new PaymentResultAttributeDTO();
            attributeDTO.setType(TradeOrderOptType.REFUND.getName());
            attributeDTO.setOperator(operator);

            UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
            cashOrderUpdate.lambda()
                    .set(TradePayoutOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradePayoutOrder::getPaymentFinishTime, LocalDateTime.now())
                    .set(TradePayoutOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradePayoutOrder::getAttribute, JSONUtil.toJsonStr(attributeDTO))
                    .eq(TradePayoutOrder::getId, order.getId());
            tradePayoutOrderService.update(cashOrderUpdate);
        }
        return true;
    }


    /**
     * 转化时间格式
     */
    private String getDisbursementTime(String disbursementTime) {
        LocalDateTime dateTime;
        if (StringUtils.isBlank(disbursementTime)) {
            dateTime = LocalDateTime.now();
        } else {
            try {
                dateTime = LocalDateTime.parse(disbursementTime, TradeConstant.DF_0);
            } catch (Exception e) {
                log.error("disbursementTime={} exception", disbursementTime, e);
                dateTime = LocalDateTime.now();
            }
        }
        return ZonedDateTime.of(dateTime, TradeConstant.ZONE_ID).format(TradeConstant.DF_3);
    }


    /**
     * 时间转化
     */
    private LocalDateTime parseDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, TradeConstant.DF_0);
        } catch (Exception ex) {
            return LocalDateTime.now();
        }
    }
}
