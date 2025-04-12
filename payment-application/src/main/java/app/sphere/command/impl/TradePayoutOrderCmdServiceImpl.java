package app.sphere.command.impl;

import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.SettleOrderCmdService;
import app.sphere.command.TradeCallBackCmdService;
import app.sphere.command.TradePayoutOrderCmdService;
import app.sphere.command.cmd.MerchantCommand;
import app.sphere.command.cmd.MoneyCommand;
import app.sphere.command.cmd.SettleAccountUpdateFrozenCmd;
import app.sphere.command.cmd.SettleRefundCmd;
import app.sphere.command.cmd.TradeCashRefundCommand;
import app.sphere.command.cmd.TradeCashSupplementCommand;
import app.sphere.command.cmd.TradeCommand;
import app.sphere.command.cmd.TradePayoutCommand;
import app.sphere.command.cmd.TradePayoutReviewCommand;
import app.sphere.command.dto.PaymentResultAttributeDTO;
import app.sphere.command.dto.TradeMerchantDTO;
import app.sphere.command.dto.TradeMoneyDTO;
import app.sphere.command.dto.TradePayoutChannelDTO;
import app.sphere.command.dto.TradePayoutDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import app.sphere.command.dto.trade.result.MerchantResultDTO;
import app.sphere.command.dto.trade.result.PaymentResultDTO;
import app.sphere.command.dto.trade.result.ReviewResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import app.sphere.manager.ChannelRouterManager;
import app.sphere.manager.FeeManager;
import app.sphere.manager.OrderNoManager;
import app.sphere.manager.dto.ChannelRouterDTO;
import app.sphere.query.MerchantQueryService;
import app.sphere.query.SettleAccountQueryService;
import app.sphere.query.dto.MerchantTradeDTO;
import app.sphere.query.param.MerchantTradeParam;
import app.sphere.query.param.SettleAccountParam;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.TradePayoutOrderRepository;
import infrastructure.sphere.config.TradeKeyConfig;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantConfig;
import infrastructure.sphere.db.entity.MerchantPaymentChannelConfig;
import infrastructure.sphere.db.entity.MerchantPayoutChannelConfig;
import infrastructure.sphere.db.entity.MerchantPayoutConfig;
import infrastructure.sphere.db.entity.PaymentChannel;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
import infrastructure.sphere.db.entity.PaymentMethod;
import infrastructure.sphere.db.entity.SettleAccount;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import infrastructure.sphere.remote.BaseDisbursementDTO;
import infrastructure.sphere.remote.ChannelEnum;
import infrastructure.sphere.remote.ChannelResult;
import infrastructure.sphere.remote.ChannelService;
import infrastructure.sphere.remote.ChannelServiceDispatcher;
import infrastructure.sphere.remote.admin.TradeExamineParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.sphere.TradeConstant;
import share.sphere.enums.CallBackStatusEnum;
import share.sphere.enums.DeductionTypeEnum;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.enums.SettleStatusEnum;
import share.sphere.enums.TradeModeEnum;
import share.sphere.enums.TradeOrderOptType;
import share.sphere.enums.TradePayoutSourceEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

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
    TradePayoutOrderRepository tradePayoutOrderRepository;
    @Resource
    TradeKeyConfig tradeKeyConfig;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    TradeCallBackCmdService tradeCallBackCmdService;
    @Resource
    OrderNoManager orderNoManager;
    @Resource
    FeeManager feeManager;
    @Resource
    MerchantQueryService merchantQueryService;
    @Resource
    SettleAccountQueryService settleAccountQueryService;
    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    ChannelRouterManager channelRouterManager;
    @Resource
    ChannelServiceDispatcher channelServiceDispatcher;
    @Resource
    SettleOrderCmdService settleOrderCmdService;

    @Override
    public TradePayoutDTO executePayout(TradePayoutCommand command) {
        log.info("trade executePayout command={}", JSONUtil.toJsonStr(command));
        return doPayout(command);
    }

    @Override
    public void executePayoutReview(TradePayoutReviewCommand command) {
        log.info("trade executePayoutReview command={}", JSONUtil.toJsonStr(command));
        doPayoutReview(command);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executeCashSupplement(TradeCashSupplementCommand command) {
        log.info("trade cashSupplement command={}", JSONUtil.toJsonStr(command));
        return doCashSupplement(command);
    }


    @Override
    public boolean executeCashRefund(TradeCashRefundCommand command) {
        log.info("trade cashRefund command={}", JSONUtil.toJsonStr(command));
        return doCashRefund(command);
    }


    // ---------------------------------------------------------------------------------------------------------

    /**
     * 代付
     */
    private TradePayoutDTO doPayout(TradePayoutCommand command) {
        // 校验校验订单
        verifyOrder(command);

        // 校验商户、如果传入回调地址，则替代
        String merchantId = command.getMerchant().getMerchantId();
        MerchantTradeParam merchantTradeParam = new MerchantTradeParam();
        merchantTradeParam.setMerchantId(command.getMerchant().getMerchantId());
        merchantTradeParam.setTradeTypeEnum(TradeTypeEnum.PAYOUT);
        merchantTradeParam.setPaymentMethod(command.getPaymentMethod());
        merchantTradeParam.setAmount(command.getMoney().getAmount());
        merchantTradeParam.setRegion(command.getRegion());
        MerchantTradeDTO merchantTradeDTO = merchantQueryService.getMerchantTradeDTO(merchantTradeParam);

        // 校验配置
        MerchantPayoutConfig merchantPayoutConfig = merchantTradeDTO.getMerchantPayoutConfig();
        if (Objects.isNull(merchantPayoutConfig)) {
            log.error("verifyMerchant merchantCashConfig is null. command={}", JSONUtil.toJsonStr(command));
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, merchantId);
        }

        // 校验渠道配置
        MerchantPaymentChannelConfig merchantPaymentChannelConfig = merchantTradeDTO.getMerchantPaymentChannelConfig();
        if (Objects.isNull(merchantPaymentChannelConfig)) {
            log.error("verifyMerchant paymentCashConfig is null. command={}", JSONUtil.toJsonStr(command));
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, merchantId);
        }


        if (StringUtils.isNotBlank(command.getCallbackUrl())) {
            merchantTradeDTO.getMerchantConfig().setFinishPayoutUrl(command.getCallbackUrl());
        }

        // 校验金额、下单入库
        TradePayoutOrder order = saveTradeCashOrder(command, merchantTradeDTO);

        // 执行冻结操作, 同步
        SettleAccountUpdateFrozenCmd frozenCmd = new SettleAccountUpdateFrozenCmd();
        frozenCmd.setTradeNo(order.getTradeNo());
        frozenCmd.setMerchantId(order.getMerchantId());
        frozenCmd.setMerchantName(order.getMerchantName());
        frozenCmd.setAccountNo(order.getAccountNo());
        frozenCmd.setCurrency(order.getCurrency());
        frozenCmd.setAmount(order.getActualAmount());
        settleAccountCmdService.handlerAccountFrozen(frozenCmd);

        // 判断是否需要人工审核

        if (configCashReview(merchantPayoutConfig, order)) {
            return pendingReview(order);
        }

        // 异步执行代付
        threadPoolTaskExecutor.execute(() -> aycExecuteCash(order, merchantTradeDTO));
        return buildCashDTO(order);
    }

    /**
     * 返回代付数据
     */
    private TradePayoutDTO buildCashDTO(TradePayoutOrder order) {
        TradePayoutDTO dto = new TradePayoutDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setOrderNo(order.getOrderNo());
        dto.setStatus(PaymentStatusEnum.PAYMENT_PROCESSING.getName());
        dto.setDisbursementTime(getDisbursementTime(null)); // 当前时间

        TradeMerchantDTO merchantDTO = new TradeMerchantDTO();
        merchantDTO.setMerchantId(order.getMerchantId());
        merchantDTO.setMerchantName(order.getMerchantName());
        merchantDTO.setAccountNo(order.getAccountNo());
        dto.setMerchant(merchantDTO);

        TradePayoutChannelDTO channelDTO = new TradePayoutChannelDTO();
        channelDTO.setBankAccount(order.getBankAccount());
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
            ChannelResult<? extends BaseDisbursementDTO> channelResult = payoutPostChannel(order);
            if (channelResult.isSuccess()) {
                BaseDisbursementDTO payload = channelResult.getPayload();
                handlerSuccessTradeResult(order, payload);

                // 校验代付平台利润
                feeManager.verifyCashPlatformProfit(order);
            }

            // 如果渠道请求失败，则抛出异常
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, order.getOrderNo());
        } catch (Exception e) {
            log.error("aycExecuteCash tradeNo={} doPayout exception", order.getTradeNo(), e);

            // 超时异常不做处理
            String errorMsg = e.getMessage();
            if (StringUtils.isNotBlank(errorMsg) && (errorMsg.contains(TradeConstant.SOCKET_TIME_OUT) || errorMsg.contains(TradeConstant.SOCKET_UNKNOWN))) {
                String exMsg = "sphere Payout"
                        + "\nTradeNo: " + order.getTradeNo()
                        + "\nOrderNo: " + order.getOrderNo()
                        + "\nSocket exception: " + errorMsg
                        + "\nPlease check";
                log.error(exMsg);
                return ;
            }

            // 异常解冻资金
            //cashUnfrozenAmount(order);

            // 更新订单状态
            errorMsg = StringUtils.isNotBlank(errorMsg) ? errorMsg : TradeConstant.ERROR_TO_CHECK;
            handlerFailedTradeResult(order, errorMsg);

            // 美化异常消息
            log.error("sphere trade doPayout {} exception errorMsg={}", order.getOrderNo(), errorMsg);

            // 回调或者抛异常
            Optional<MerchantResultDTO> merchantResultDTO = Optional.of(order).map(TradePayoutOrder::getTradeResult)
                    .map(re -> JSONUtil.toBean(re, TradeResultDTO.class))
                    .map(TradeResultDTO::getMerchantResult);
//            LocalDateTime tradeLocalTime = Optional.of(order).map(TradePayoutOrder::getTradeTime).orElse(LocalDateTime.now());
            TradePayoutSourceEnum sourceEnum = TradePayoutSourceEnum.codeToEnum(order.getSource());

            // 解析回调地址
            String finishCashUrl = merchantResultDTO.map(MerchantResultDTO::getFinishCashUrl).orElse(null);
            TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
            bodyDTO.setTradeNo(order.getTradeNo());
            bodyDTO.setOrderNo(order.getOrderNo());
            bodyDTO.setMerchantId(order.getMerchantId());
            bodyDTO.setMerchantName(order.getMerchantName());
            bodyDTO.setPaymentMethod(order.getPaymentMethod());
            bodyDTO.setStatus(TradeStatusEnum.TRADE_FAILED.getMerchantStatus());
            bodyDTO.setTransactionTime(LocalDateTime.now().toString());

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
     * 校验订单是否已存在
     */
    private void verifyOrder(TradePayoutCommand command) {
        String orderNo = command.getOrderNo();

        QueryWrapper<TradePayoutOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.select("outer_no as orderNo");
        payOrderQuery.lambda().eq(TradePayoutOrder::getOrderNo, orderNo).last(TradeConstant.LIMIT_1);
        TradePayoutOrder order = tradePayoutOrderRepository.getOne(payOrderQuery);
        Assert.isNull(order, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, orderNo));
    }


    /**
     * 代付审核后处理
     */
    private boolean doPayoutReview(TradePayoutReviewCommand command) {
        String tradeNo = command.getTradeNo();

        // 校验订单是否存在
        QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePayoutOrder order = tradePayoutOrderRepository.getOne(cashOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo));

        // 判断是否在审核中状态
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!TradeStatusEnum.TRADE_REVIEW.equals(tradeStatusEnum)) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo);
        }

        // 先解析TradeResult
        TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());

        // 构建审核结果
        ReviewResultDTO reviewResultDTO = new ReviewResultDTO();
        reviewResultDTO.setReviewStatus(command.isReviewStatus());
        reviewResultDTO.setReviewTime(System.currentTimeMillis());
        reviewResultDTO.setReviewMsg(command.getReviewMsg());
        tradeResultDTO.setReviewResult(reviewResultDTO);

        // 审核驳回
        if (!command.isReviewStatus()) {
            // 设置失败
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(command.getReviewMsg());

            // 解冻资金, 此处如果异常，完了，状态一直在审核中，冻结的金额也没有解冻，但至少有消息，该如何手动解决呢？ 不知道
            //cashUnfrozenAmount(order);

            // 更新订单状态
            UpdateWrapper<TradePayoutOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(TradePayoutOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradePayoutOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradePayoutOrder::getPaymentFinishTime, LocalDateTime.now())
                    .set(TradePayoutOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .eq(TradePayoutOrder::getId, order.getId());
            return tradePayoutOrderRepository.update(updateWrapper);
        }

        try {
            // 审核通过 发起代付    
            ChannelResult<? extends BaseDisbursementDTO> channelResult = payoutPostChannel(order);
            if (channelResult.isSuccess()) {
                BaseDisbursementDTO payload = channelResult.getPayload();
                handlerSuccessTradeResult(order, payload);
            }
            return true;
        } catch (Exception e) {
            log.error("doPayoutReview tradeNo={} exception:", command.getTradeNo(), e);

            // 异常解冻资金
            //cashUnfrozenAmount(order);

            // 更新订单状态
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;
            handlerFailedTradeResult(order, errorMsg);

            // 美化异常消息
            log.error("doPayoutReview tradeNo={} exception errorMsg={}", command.getTradeNo(), errorMsg);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, order.getOrderNo(), errorMsg);
        }
    }

    /**
     * 代付下单, 此步骤保存了商户的配置信息到tradeResult
     */
    private TradePayoutOrder saveTradeCashOrder(TradePayoutCommand command, MerchantTradeDTO merchantDTO) {
        Merchant merchant = merchantDTO.getMerchant();

        // 渠道信息 FIX channelName不要超过10
        // 商户名称 FIX 取值BrandName
        String merchantName = Optional.of(command).map(TradeCommand::getMerchant)
                .map(MerchantCommand::getMerchantName)
                .orElse(merchant.getBrandName());

        // 账户号-存在
        SettleAccountParam accountParam = new SettleAccountParam();
        accountParam.setMerchantId(command.getMerchant().getMerchantId());
        accountParam.setRegion(command.getRegion());
        SettleAccount settleAccount = settleAccountQueryService.getSettleAccount(accountParam);
        if (Objects.isNull(settleAccount)) {
            throw new PaymentException("账户号不存在");
        }

        // 构建代付提前交易结果
        MoneyCommand money = command.getMoney();
        TradeResultDTO tradeResultDTO = getTradeResultDTO(merchantDTO, money.getAmount());
        MerchantResultDTO merchantResult = tradeResultDTO.getMerchantResult();
        log.info("saveTradeCashOrder tradeResultDTO={}", JSONUtil.toJsonStr(tradeResultDTO));
        BigDecimal merchantFee = feeManager.calculateMerchantFee(command.getMoney().getAmount(), merchantResult.getSingleRate(), merchantResult.getSingleFee());
        BigDecimal accountAmount = feeManager.calculateAccountAmount(DeductionTypeEnum.DEDUCTION_INTERNAL, command.getMoney().getAmount(), merchantFee);
        BigDecimal actualAmount = feeManager.calculateActualAmount(DeductionTypeEnum.codeToEnum(merchantResult.getDeductionType()), command.getMoney().getAmount(), merchantFee);

        // 构建金额, 如果内扣，那么减去手续费可能会导致到账金额小于0，则抛出异常
        if (accountAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.error("saveTradeCashOrder accountAmount is less than zero. accountAmount={}", accountAmount);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR,
                    accountAmount.setScale(0, RoundingMode.UP).toString());
        }

        // 构建代付订单
        String orderNo = command.getOrderNo();
        String tradeNo = orderNoManager.getTradeNo(command.getRegion(), TradeTypeEnum.PAYOUT, merchant.getMerchantId());

        TradePayoutOrder order = new TradePayoutOrder();
        order.setTradeNo(tradeNo);
        order.setOrderNo(orderNo);
        order.setPurpose(command.getPurpose());
        order.setProductDetail(command.getProductDetail());
        order.setPaymentMethod(command.getPaymentMethod());
        order.setChannelCode(null);
        order.setBankCode(command.getBankCode());
        order.setBankAccount(command.getBankAccount());
        order.setMerchantId(command.getMerchant().getMerchantId());
        order.setMerchantName(merchantName);
        order.setAccountNo(settleAccount.getAccountNo());

        // 金额 实扣金额 商户分润 商户手续费 商户到账金额
        order.setCurrency(money.getCurrency());
        order.setAmount(money.getAmount());
        order.setActualAmount(actualAmount);
        order.setMerchantFee(merchantFee);
        order.setMerchantProfit(BigDecimal.ZERO);
        order.setAccountAmount(accountAmount);

        order.setPayerInfo(JSONUtil.toJsonStr(command.getPayer()));
        order.setReceiverInfo(JSONUtil.toJsonStr(command.getReceiver()));
        order.setTradeTime(System.currentTimeMillis());
        order.setTradeStatus(TradeStatusEnum.TRADE_INIT.getCode());
        order.setTradeResult(JSONUtil.toJsonStr(tradeResultDTO));
        order.setPaymentStatus(PaymentStatusEnum.PAYMENT_PENDING.getCode());

        order.setSettleStatus(SettleStatusEnum.SETTLE_TODO.getCode());
        order.setCallBackStatus(CallBackStatusEnum.CALLBACK_TODO.getCode());
        order.setSource(command.getTradePayoutSourceEnum().getCode());
        order.setVersion(TradeConstant.INIT_VERSION);
        order.setRegion(command.getRegion());
        order.setAttribute("{}");
        order.setCreateTime(LocalDateTime.now());
        tradePayoutOrderRepository.save(order);
        return order;
    }

    /**
     * 解析支付前的交易结果
     */
    private TradeResultDTO getTradeResultDTO(MerchantTradeDTO merchantDTO, BigDecimal amount) {
        TradeResultDTO tradeResultDTO = new TradeResultDTO();

        MerchantConfig merchantConfig = merchantDTO.getMerchantConfig();
        MerchantPayoutConfig merchantPayoutConfig = merchantDTO.getMerchantPayoutConfig();
        MerchantPayoutChannelConfig merchantPayoutChannelConfig = merchantDTO.getMerchantPayoutChannelConfig();

        BigDecimal singleFee = merchantPayoutChannelConfig.getSingleFee();
        BigDecimal singleRate = merchantPayoutChannelConfig.getSingleRate();
        Integer deductionType = merchantPayoutConfig.getDeductionType();

        // 商户结果
        MerchantResultDTO merchantResult = new MerchantResultDTO();
        merchantResult.setFinishCashUrl(merchantConfig.getFinishPayoutUrl());
        merchantResult.setDeductionType(deductionType);
        merchantResult.setSingleFee(singleFee);
        merchantResult.setSingleRate(singleRate);
        merchantResult.setSettleType(merchantPayoutChannelConfig.getSettleType());
        merchantResult.setSettleTime(merchantPayoutChannelConfig.getSettleTime());

        tradeResultDTO.setMerchantResult(merchantResult);
        tradeResultDTO.setPaymentResult(null);
        return tradeResultDTO;
    }


    /**
     * 满足人工审核条件
     */
    private boolean configCashReview(MerchantPayoutConfig merchantCashConfig, TradePayoutOrder order) {
        // 通过开关和金额判断, 如果开关开启 且金额大于设置金额
        Boolean cashReview = Optional.of(merchantCashConfig)
                .map(MerchantPayoutConfig::isReview)
                .orElse(Boolean.TRUE);

        // 审核开关 且 金额大于审核金额
        return cashReview;
    }


    /**
     * 发送消息到admin。等待审核
     */
    private TradePayoutDTO pendingReview(TradePayoutOrder order) {
        // 构建审核参数
        TradeExamineParam examineParam = new TradeExamineParam();
        examineParam.setTradeNo(order.getTradeNo());
        examineParam.setOrderNo(order.getOrderNo());
        examineParam.setMerchantId(order.getMerchantId());
        examineParam.setMerchantName(order.getMerchantName());
        examineParam.setProductDetail(order.getProductDetail());
        examineParam.setPaymentMethod(order.getPaymentMethod());
        examineParam.setCurrency(order.getCurrency());
        examineParam.setAmount(order.getAmount().toString() + " " + order.getCurrency());
        examineParam.setPayerInfo(order.getPayerInfo());
        examineParam.setReceiverInfo(order.getReceiverInfo());
        examineParam.setApplyOperator(order.getMerchantName());
        examineParam.setRemark(order.getAttribute());

        // 发送消息去审核
        String message = JSONUtil.toJsonStr(examineParam);
        log.info("pendingReview examineParam={}", message);

        // 更新订单审核中
        order.setTradeStatus(TradeStatusEnum.TRADE_REVIEW.getCode());
        UpdateWrapper<TradePayoutOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(TradePayoutOrder::getTradeStatus, order.getTradeStatus())
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePayoutOrder::getId, order.getId());
        tradePayoutOrderRepository.update(updateWrapper);

        // 构建返回数据
        TradePayoutDTO dto = new TradePayoutDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setStatus(TradeStatusEnum.codeToEnum(order.getTradeStatus()).getMerchantStatus());
        dto.setDisbursementTime(null);
        dto.setOrderNo(order.getOrderNo());

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
        cashChannelDTO.setBankAccount(order.getBankAccount());
        cashChannelDTO.setAccountName(order.getMerchantName());
        dto.setChannel(cashChannelDTO);
        return dto;
    }

    /**
     * 调用外部渠道
     */
    private ChannelResult<? extends BaseDisbursementDTO> payoutPostChannel(TradePayoutOrder order) {
        //路由
        ChannelRouterDTO channelRouterDTO = channelRouterManager.disbursementRouter(order);

        PaymentChannel channel = channelRouterDTO.getPaymentChannel();
        PaymentMethod method = channelRouterDTO.getPaymentMethod();
        PaymentChannelMethod channelMethod = channelRouterDTO.getPaymentChannelMethod();
        String channelCode = channel.getChannelCode();

        //执行支付
        //choose method and payment channelMethod then transaction if exception opt order status to fail else ing
        ChannelService channelService = channelServiceDispatcher.getService(ChannelEnum.codeToEnum(channelCode));
        log.info("disbursement tradeNo={} channel service={}", order.getTradeNo(), channelService.getChannelName());

        //发起出款
        ChannelResult<? extends BaseDisbursementDTO> channelResult = channelService.disbursement(channel, method, channelMethod, order);
        log.info("disbursement tradeNo={} channel result={}", order.getTradeNo(), JSONUtil.toJsonStr(channelResult));

        return channelResult;
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
        tradePayoutOrderRepository.update(cashOrderUpdate);
    }

    /**
     * 支付返回，同步插入
     */
    private void handlerSuccessTradeResult(TradePayoutOrder order, BaseDisbursementDTO disbursementDTO) {
        log.info("handlerSuccessTradeResult orderNo={}", order.getTradeNo());

        // 解析交易前预存的结果
        TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());
        tradeResultDTO.setSuccess(true);

        // 渠道请求结果
        PaymentResultDTO paymentResult = Optional.of(tradeResultDTO).map(TradeResultDTO::getPaymentResult)
                .orElse(new PaymentResultDTO());
        paymentResult.setChannelOrderNo(disbursementDTO.getChannelOrderNo());

        // 支付执行后, PaymentResult下单时已经设置, 现在是补充
        tradeResultDTO.setPaymentResult(paymentResult);

        // 更新交易订单：交易状态、渠道编码、交易结果
        UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
        cashOrderUpdate.lambda()
                .set(TradePayoutOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(TradePayoutOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePayoutOrder::getId, order.getId());
        tradePayoutOrderRepository.update(cashOrderUpdate);
    }

    /**
     * 根据订单号进行代付补单
     */
    private boolean doCashSupplement(TradeCashSupplementCommand command) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePayoutOrder order = tradePayoutOrderRepository.getOne(cashOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo));

        // 如果下单都没成功，谈何补单
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!tradeStatusEnum.equals(TradeStatusEnum.TRADE_SUCCESS)) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo);
        }

        // 如果是支付成功状态，不必进行补单
        // 如果还未支付/支付中，能进行补单 可能原因是网络未收到回调消息等其他
        // 如果支付状态失败终态，不能进行补单
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_SUCCESS)) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo);
        } else if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_FAILED)) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo);
        } else {
            TradeResultDTO tradeResultDTO = Optional.of(order).map(TradePayoutOrder::getTradeResult)
                    .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                    .orElse(new TradeResultDTO());
            tradeResultDTO.setSuccess(true);

            // 结算补单
            settleOrderCmdService.supplement(null);
            log.info("doCashSupplement tradeNo={}", tradeNo);

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
            tradePayoutOrderRepository.update(cashOrderUpdate);
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
        TradePayoutOrder order = tradePayoutOrderRepository.getOne(cashOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo));

        // 如果交易都没成功，谈何退单
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!tradeStatusEnum.equals(TradeStatusEnum.TRADE_SUCCESS)) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo);
        }

        // 如果还未支付/支付中，直接设置为失败
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_PENDING)
                || paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_PROCESSING)) {
            // 更新订单状态
            PaymentResultAttributeDTO attributeDTO = new PaymentResultAttributeDTO();
            attributeDTO.setType(TradeOrderOptType.REFUND.getName());
            attributeDTO.setOperator(operator);

            TradeResultDTO tradeResultDTO = Optional.of(order).map(TradePayoutOrder::getTradeResult)
                    .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                    .orElse(new TradeResultDTO());
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError("set refund operate by " + operator);

            UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
            cashOrderUpdate.lambda()
                    .set(TradePayoutOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradePayoutOrder::getPaymentFinishTime, LocalDateTime.now())
                    .set(TradePayoutOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradePayoutOrder::getAttribute, JSONUtil.toJsonStr(attributeDTO))
                    .eq(TradePayoutOrder::getId, order.getId());
            tradePayoutOrderRepository.update(cashOrderUpdate);

            // 如果资金有冻结则需要解冻, 如果没有冻结, 解冻失败也属于正常
            SettleRefundCmd refundCmd = new SettleRefundCmd();
            refundCmd.setTradeNo(order.getTradeNo());
            settleOrderCmdService.refund(refundCmd);
        }

        // 如果是支付失败状态，不必进行退单
        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_FAILED)) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo);
        }

        // 如果已经是成功状态，强制失败
        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_SUCCESS)) {
            SettleRefundCmd refundCmd = new SettleRefundCmd();
            refundCmd.setTradeNo(order.getTradeNo());
            settleOrderCmdService.refund(refundCmd);

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
            tradePayoutOrderRepository.update(cashOrderUpdate);
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
}
