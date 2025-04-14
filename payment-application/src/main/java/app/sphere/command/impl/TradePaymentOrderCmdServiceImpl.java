package app.sphere.command.impl;

import app.sphere.command.SettleOrderCmdService;
import app.sphere.command.TradePaymentOrderCmdService;
import app.sphere.command.cmd.*;
import app.sphere.command.dto.*;
import app.sphere.command.dto.trade.result.*;
import app.sphere.manager.*;
import app.sphere.manager.dto.ChannelRouterDTO;
import app.sphere.query.MerchantQueryService;
import app.sphere.query.SettleAccountQueryService;
import app.sphere.query.dto.MerchantTradeDTO;
import app.sphere.query.dto.TradeCashierStyleDTO;
import app.sphere.query.param.*;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.TradePaymentLinkOrderRepository;
import domain.sphere.repository.TradePaymentOrderRepository;
import infrastructure.sphere.db.entity.*;
import infrastructure.sphere.remote.channel.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.sphere.TradeConstant;
import share.sphere.enums.*;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.math.BigDecimal;
import java.time.*;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class TradePaymentOrderCmdServiceImpl implements TradePaymentOrderCmdService {

    @Resource
    TradePaymentOrderRepository tradePaymentOrderRepository;


    @Resource
    TradePaymentLinkOrderRepository tradePaymentLinkOrderRepository;

    @Resource
    ChannelRouterManager channelRouterManager;
    @Resource
    ChannelServiceDispatcher channelServiceDispatcher;
    @Resource
    OrderNoManager orderNoManager;
    @Resource
    FeeManager feeManager;
    @Resource
    SettleAccountQueryService settleAccountQueryService;

    @Resource
    MerchantQueryService merchantQueryService;
    @Resource
    SettleOrderCmdService settleOrderCmdService;

    @Value("${trade.cashier.path:123}")
    String cashierPath;

    @Override
    public String executePaymentLink(TradePaymentCmd command) {
        log.info("executePaymentLink command={}", JSONUtil.toJsonStr(command));
        return doPaymentLink(command);
    }

    @Override
    public TradePaymentDTO executeApiPayment(TradePaymentCmd command) {
        log.info("executeApiPayment command={}", JSONUtil.toJsonStr(command));
        return doApiPay(command);
    }

    @Override
    public TradeCashierPaymentDTO executeCashierPay(TradeCashierPaymentCmd command) {
        log.info("executeCashierPay command={}", JSONUtil.toJsonStr(command));
        return doCashierPay(command);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executePaymentSupplement(TradePaymentSupplementCmd command) {
        log.info("executePaymentSupplement command={}", JSONUtil.toJsonStr(command));
        return doPaySupplement(command);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executePaymentRefund(TradePaymentRefundCmd command) {
        log.info("executePaymentRefund command={}", JSONUtil.toJsonStr(command));
        return doPayRefund(command);
    }


    // --------------------------------------------------------------------------------------------------------------


    /**
     * 创建支付链接
     */
    private String doPaymentLink(TradePaymentCmd command) {
        // 构建 payCommand
        TradePaymentDTO dto = doApiPay(command);
        String paymentLink = cashierPath + "?tradeNo=" + dto.getTradeNo();

        // 保存交易链接
        saveTradePaymentLinkOrder(command, paymentLink);
        return paymentLink;
    }

    /**
     * 收款操作
     */
    private TradePaymentDTO doApiPay(TradePaymentCmd command) {
        // 校验订单
        verifyOrder(command);

        // 下单入库
        TradePaymentOrder order = saveTradePayOrder(command);

        // 根据支付方式进行执行, 如果支付方式空，则返回收银台地址
        String paymentMethod = order.getPaymentMethod();
        if (StringUtils.isBlank(paymentMethod)) {
            return buildCashier(order);
        }

        // 如果是OVO 且手机号未传入, 则返回收银台
        boolean ovo = StringUtils.containsIgnoreCase(paymentMethod, "OVO");
        boolean phone = Optional.of(command).map(TradePaymentCmd::getPayer).map(PayerCommand::getPhone).isEmpty();
        if (ovo && phone) {
            log.info("trade doApiPay paymentMethod is OVO. return cashier. tradeNo={}", order.getTradeNo());
            return buildCashier(order);
        }

        // 发起收款支付
        try {
            ChannelResult<? extends BaseTransactionDTO> channelResult = payPostChannel(order);
            if (channelResult.isSuccess()) {
                BaseTransactionDTO payload = channelResult.getPayload();
                // 处理支付成功
                handlerSuccessTradeResult(payload, order);

                // 构建返回数据
                TradePaymentDTO dto = new TradePaymentDTO();
                dto.setOrderNo(order.getOrderNo());
                dto.setOrderNo(order.getOrderNo());
                dto.setStatus(PaymentStatusEnum.codeToEnum(order.getPaymentStatus()).name());
                dto.setTransactionTime(ZonedDateTime.of(LocalDateTime.now(), TradeConstant.ZONE_ID).format(TradeConstant.DF_3)); //时间格式问题

                TradeMerchantDTO merchant = new TradeMerchantDTO();
                merchant.setMerchantId(order.getMerchantId());
                merchant.setMerchantName(order.getMerchantName());
                merchant.setAccountNo(order.getAccountNo());
                dto.setMerchant(merchant);

                TradeMoneyDTO money = new TradeMoneyDTO();
                money.setCurrency(order.getCurrency());
                money.setAmount(order.getAmount());
                money.setFee(order.getMerchantFee());
                dto.setMoney(money);

                TradePayChannelDTO channel = new TradePayChannelDTO();
                channel.setPaymentMethod(order.getPaymentMethod());
                channel.setQrString(payload.getChannelQr());
                channel.setPaymentUrl(payload.getChannelPaymentUrl());
                return dto;
            }

            throw new PaymentException(channelResult.getErrorMsg());
        } catch (Exception e) {
            log.error("trade doApiPay {} exception:", order.getTradeNo(), e);

            // 支付异常，处理订单状态
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;
            handlerFailedTradeResult(order, errorMsg, null);

            // 美化异常消息
            log.error("trade doApiPay {} exception errorMsg={}", order.getTradeNo(), errorMsg);

            // 超时或者渠道异常，收款抛异常
            if (errorMsg.contains(TradeConstant.SOCKET_TIME_OUT) || errorMsg.contains(TradeConstant.SOCKET_UNKNOWN)) {
                String exMsg = "sphere Payout"
                        + "\nTradeNo: " + order.getTradeNo()
                        + "\norderNo: " + order.getOrderNo()
                        + "\nSocket exception: " + errorMsg
                        + "\nPlease check";
                log.error(exMsg);
                throw new PaymentException(ExceptionCode.PAYMENT_TIMEOUT, order.getTradeNo());
            }
            
            throw new PaymentException(ExceptionCode.SYSTEM_BUSY, command.getOrderNo(), errorMsg);
        }
    }


    /**
     * 判断订单是否已经存在 使用外部单号
     */
    private void verifyOrder(TradePaymentCmd command) {
        String orderNo = command.getOrderNo();
        QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.select("order_no as orderNo");
        payOrderQuery.lambda().eq(TradePaymentOrder::getOrderNo, orderNo).last(TradeConstant.LIMIT_1);
        TradePaymentOrder order = tradePaymentOrderRepository.getOne(payOrderQuery);
        Assert.isNull(order, () -> new PaymentException(ExceptionCode.PARAM_ERROR, orderNo));
    }

    /**
     * 收银台收款
     */
    private TradeCashierPaymentDTO doCashierPay(TradeCashierPaymentCmd command) {
        String tradeNo = command.getTradeNo();

        // 校验订单
        QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePaymentOrder order = tradePaymentOrderRepository.getOne(payOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FOUND, tradeNo));

        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        log.info("doCashierPay tradeStatusEnum={}", tradeStatusEnum);
        // 校验状态，如果订单已经失败
        if (TradeStatusEnum.TRADE_FAILED.equals(tradeStatusEnum)) {
            throw new PaymentException(ExceptionCode.TRADE_ORDER_HAS_FAILED, tradeNo);
        }
        // 校验状态，如果订单已经过期
        if (TradeStatusEnum.TRADE_EXPIRED.equals(tradeStatusEnum)) {
            throw new PaymentException(ExceptionCode.TRADE_ORDER_HAS_EXPIRED, tradeNo);
        }
        // 收银台样式 使用缓存
        TradeCashierStyleDTO styleDTO = getTradeCashierStyleDTO(order);
        // 校验状态，如果订单已经成功
        if (order.getTradeStatus().equals(TradeStatusEnum.TRADE_SUCCESS.getCode())) {
            return buildSuccessTradeCashier(order, styleDTO);
        }

        // 再次校验状态，如果订单已经超时
        checkExpiredAgain(order);

        // 设置支付方式 在此之前accountNo应该是null, 但支付的时候就可以更新进去，好坑爹哦
        MerchantTradeParam merchantTradeParam = new MerchantTradeParam();
        merchantTradeParam.setMerchantId(order.getMerchantId());
        merchantTradeParam.setTradeTypeEnum(TradeTypeEnum.PAYMENT);
        merchantTradeParam.setPaymentMethod(command.getPaymentMethod());
        merchantTradeParam.setAmount(order.getAmount());
        merchantTradeParam.setRegion(order.getRegion());
        MerchantTradeDTO merchantTradeDTO = merchantQueryService.getMerchantTradeDTO(merchantTradeParam);
        MerchantPaymentChannelConfig merchantPaymentChannelConfig = merchantTradeDTO.getMerchantPaymentChannelConfig();

        // 校验账户号
        SettleAccountParam accountParam = new SettleAccountParam();
        accountParam.setMerchantId(order.getMerchantId());
        accountParam.setRegion(order.getRegion());
        SettleAccount settleAccount = settleAccountQueryService.getSettleAccount(accountParam);
        if (Objects.isNull(settleAccount)) {
            throw new PaymentException("账户号不存在");
        }

        order.setPaymentMethod(command.getPaymentMethod());
        order.setChannelCode(merchantPaymentChannelConfig.getChannelCode());
        order.setChannelName(merchantPaymentChannelConfig.getChannelName());
        order.setAccountNo(settleAccount.getAccountNo());

        // 设置金额，商户分润, 商户手续费, 商户到账金额
        TradeResultDTO tradeResultDTO = getTradeResultDTO(merchantTradeDTO);
        MerchantResultDTO merchantResult = tradeResultDTO.getMerchantResult();
        BigDecimal merchantFee = feeManager.calculateMerchantFee(order.getAmount(), merchantResult.getSingleRate(), merchantResult.getSingleFee());
        BigDecimal accountAmount = feeManager.calculateAccountAmount(DeductionTypeEnum.DEDUCTION_INTERNAL, order.getAmount(), merchantFee);
        order.setMerchantFee(merchantFee);
        order.setMerchantProfit(BigDecimal.ZERO);
        order.setAccountAmount(accountAmount);
        order.setTradeResult(JSONUtil.toJsonStr(tradeResultDTO));

        UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();

        // 如果手机号不为空, 则替换手机号 因为Woo, 只更新手机号(OVO支付方式的时候, 因为其他都已经在交易的时候保存)
        if (StringUtils.isNotBlank(command.getPhone())) {
            PayerCommand payerCommand = Optional.of(order).map(TradePaymentOrder::getPayerInfo)
                    .map(e -> JSONUtil.toBean(e, PayerCommand.class))
                    .orElse(new PayerCommand());
            payerCommand.setPhone(command.getPhone());
            order.setPayerInfo(JSONUtil.toJsonStr(payerCommand));
            payOrderUpdate.lambda().set(TradePaymentOrder::getPayerInfo, order.getPayerInfo());
        }

        // 更新商户账户号 & 交易结果 & 收款人手机号
        payOrderUpdate.lambda()
                .set(TradePaymentOrder::getAmount, order.getAmount())
                .set(TradePaymentOrder::getPaymentMethod, order.getPaymentMethod())
                .set(TradePaymentOrder::getAccountNo, order.getAccountNo())
                .set(TradePaymentOrder::getChannelCode, order.getChannelCode())
                .set(TradePaymentOrder::getMerchantProfit, order.getMerchantProfit())
                .set(TradePaymentOrder::getMerchantFee, order.getMerchantFee())
                .set(TradePaymentOrder::getAccountAmount, order.getAccountAmount());

        try {
            // 发起收银台收款支付
            ChannelResult<? extends BaseTransactionDTO> channelResult = payPostChannel(order);
            if (channelResult.isSuccess()) {
                BaseTransactionDTO payload = channelResult.getPayload();
                // 处理支付成功
                handlerSuccessTradeResult(payload, order);

                // 构建返回数据
                TradePaymentDTO dto = new TradePaymentDTO();
                dto.setOrderNo(order.getOrderNo());
                dto.setOrderNo(order.getOrderNo());
                dto.setStatus(PaymentStatusEnum.codeToEnum(order.getPaymentStatus()).name());
                dto.setTransactionTime(ZonedDateTime.of(LocalDateTime.now(), TradeConstant.ZONE_ID).format(TradeConstant.DF_3)); //时间格式问题

                TradeMerchantDTO merchant = new TradeMerchantDTO();
                merchant.setMerchantId(order.getMerchantId());
                merchant.setMerchantName(order.getMerchantName());
                merchant.setAccountNo(order.getAccountNo());
                dto.setMerchant(merchant);

                TradeMoneyDTO money = new TradeMoneyDTO();
                money.setCurrency(order.getCurrency());
                money.setAmount(order.getAmount());
                money.setFee(order.getMerchantFee());
                dto.setMoney(money);

                TradePayChannelDTO channel = new TradePayChannelDTO();
                channel.setPaymentMethod(order.getPaymentMethod());
                channel.setQrString(payload.getChannelQr());
                channel.setPaymentUrl(payload.getChannelPaymentUrl());
                return buildTradeCashierPayDTO(dto, order, styleDTO);
            }

            throw new PaymentException(channelResult.getErrorMsg());
        } catch (Exception e) {
            log.error("trade doCashierPay tradeNo={} exception:", tradeNo, e);

            // 处理订单状态
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;
            handlerFailedTradeResult(order, errorMsg, payOrderUpdate);

            // 异常消息
            log.error("trade doCashierPay tradeNo={} exception errorMsg={}", tradeNo, errorMsg);

            // 超时或者渠道异常，收款抛异常
            if (errorMsg.contains(TradeConstant.SOCKET_TIME_OUT) || errorMsg.contains(TradeConstant.SOCKET_UNKNOWN)) {
                String exMsg = "sphere Payin"
                        + "\nTradeNo: " + order.getTradeNo()
                        + "\norderNo: " + order.getOrderNo()
                        + "\nSocket exception: " + errorMsg
                        + "\nPlease check";
                log.error(exMsg);
                throw new PaymentException(ExceptionCode.PAYMENT_TIMEOUT, tradeNo);
            }
            
            throw new PaymentException(ExceptionCode.SYSTEM_BUSY, tradeNo, errorMsg);
        }
    }


    /**
     * 再次确认订单是否超时
     */
    private void checkExpiredAgain(TradePaymentOrder order) {
        Integer expiryPeriod = Optional.of(order).map(TradePaymentOrder::getAttribute)
                .map(e -> JSONUtil.toBean(e, TradePaymentAttributeDTO.class))
                .map(TradePaymentAttributeDTO::getExpiryPeriod)
                .orElse(TradeConstant.TRADE_EXPIRY_PERIOD_MAX);
        // 方法1：通过 Instant 转换
        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        log.info("doCashierPay expiryPeriod={}, timestamp={}", expiryPeriod, timestamp);
        if (timestamp - order.getTradeTime() > expiryPeriod) {
            Integer tradeStatus = TradeStatusEnum.TRADE_EXPIRED.getCode();
            UpdateWrapper<TradePaymentOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(TradePaymentOrder::getTradeStatus, tradeStatus)
                    .eq(TradePaymentOrder::getId, order.getId());
            tradePaymentOrderRepository.update(updateWrapper);

            // 订单来源
            TradePaymentSourceEnum tradePaymentSourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
            log.info("doCashierPay tradePaySourceEnum={}", tradePaymentSourceEnum.name());
            if (TradePaymentSourceEnum.PAY_LINK.equals(tradePaymentSourceEnum)) {
                Integer paymentLinkStatus = TradePaymentLinkStatusEnum.PAYMENT_LINK_EXPIRED.getCode();
                UpdateWrapper<TradePaymentLinkOrder> linkUpdate = new UpdateWrapper<>();
                linkUpdate.lambda().set(TradePaymentLinkOrder::getLinkStatus, paymentLinkStatus)
                        .eq(TradePaymentLinkOrder::getLinkNo, order.getOrderNo());
                tradePaymentLinkOrderRepository.update(linkUpdate);
            }
            throw new PaymentException(ExceptionCode.TRADE_ORDER_HAS_EXPIRED, order.getTradeNo());
        }
    }


    /**
     * SuccessTradeCashier
     */
    private TradeCashierPaymentDTO buildSuccessTradeCashier(TradePaymentOrder order, TradeCashierStyleDTO styleDTO) {
        LocalDateTime tradeTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(order.getTradeTime()),
                ZoneId.systemDefault()
        );

        TradeCashierPaymentDTO dto = new TradeCashierPaymentDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setTradeTime(tradeTime.toString());
        dto.setMerchantId(order.getMerchantId());
        dto.setMerchantName(order.getMerchantName());
        dto.setCurrency(order.getCurrency());
        dto.setAmount(order.getAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        //dto.setExpiryPeriod(getExpiryPeriod(order));
        dto.setMethodResult(getMethodResult(order.getTradeResult()));
        dto.setStyle(styleDTO);
        return dto;
    }

    /**
     * 收银台信息
     */
    private TradePaymentDTO buildCashier(TradePaymentOrder order) {
        TradeMerchantDTO merchantDTO = new TradeMerchantDTO();
        merchantDTO.setMerchantId(order.getMerchantId());
        merchantDTO.setMerchantName(order.getMerchantName());
        merchantDTO.setAccountNo(order.getAccountNo());

        TradeMoneyDTO moneyDTO = new TradeMoneyDTO();
        moneyDTO.setCurrency(order.getCurrency());
        moneyDTO.setAmount(order.getAmount());

        TradePayChannelDTO channel = new TradePayChannelDTO();
        channel.setPaymentMethod(order.getPaymentMethod());
        channel.setPaymentUrl(cashierPath + "?tradeNo=" + order.getTradeNo());

        TradePaymentDTO tradePaymentDTO = new TradePaymentDTO();
        tradePaymentDTO.setOrderNo(order.getOrderNo());
        tradePaymentDTO.setTradeNo(order.getTradeNo());
        tradePaymentDTO.setStatus(TradeStatusEnum.TRADE_INIT.getMerchantStatus());
        tradePaymentDTO.setTransactionTime(null); //FIXME
        tradePaymentDTO.setMerchant(merchantDTO);
        tradePaymentDTO.setMoney(moneyDTO);
        tradePaymentDTO.setChannel(channel);

        log.info("buildCashier tradePayDTO={}", JSONUtil.toJsonStr(tradePaymentDTO));
        return tradePaymentDTO;
    }


    /**
     * 账户号
     */
    private SettleAccount getSettleAccount(TradePaymentCmd command) {
        SettleAccountParam accountParam = new SettleAccountParam();
        accountParam.setMerchantId(command.getMerchant().getMerchantId());
        accountParam.setRegion(command.getRegion());
        SettleAccount settleAccount = settleAccountQueryService.getSettleAccount(accountParam);
        if (Objects.isNull(settleAccount)) {
            throw new PaymentException("账户号不存在");
        }
        return settleAccount;
    }


    /**
     * 收单交易下单
     */
    private TradePaymentOrder saveTradePayOrder(TradePaymentCmd command) {
        String paymentMethod = command.getPaymentMethod();

        // 校验商户
        MerchantTradeDTO merchantTradeDTO = null;
        if (StringUtils.isAllBlank(paymentMethod)) {
            MerchantIdParam merchantIdParam = new MerchantIdParam();
            merchantIdParam.setMerchantId(command.getMerchant().getMerchantId());
            Merchant merchant = merchantQueryService.getMerchant(merchantIdParam);
            merchantTradeDTO = new MerchantTradeDTO();
            merchantTradeDTO.setMerchant(merchant);

        } else {
            MerchantTradeParam merchantTradeParam = new MerchantTradeParam();
            merchantTradeParam.setMerchantId(command.getMerchant().getMerchantId());
            merchantTradeParam.setTradeTypeEnum(TradeTypeEnum.PAYMENT);
            merchantTradeParam.setPaymentMethod(command.getPaymentMethod());
            merchantTradeParam.setAmount(command.getMoney().getAmount());
            merchantTradeParam.setRegion(command.getRegion());
            merchantTradeDTO =  merchantQueryService.getMerchantTradeDTO(merchantTradeParam);
        }
        if (StringUtils.isNotBlank(command.getCallbackUrl())) {
            merchantTradeDTO.getMerchantConfig().setFinishPaymentUrl(command.getCallbackUrl());
        }
        if (StringUtils.isNotBlank(command.getRedirectUrl())) {
            merchantTradeDTO.getMerchantConfig().setFinishRedirectUrl(command.getRedirectUrl());
        }
        Merchant merchant = merchantTradeDTO.getMerchant();
        String merchantId = merchant.getMerchantId();
        String brandName = merchant.getBrandName();
        String merchantName = Optional.of(command).map(TradeCommand::getMerchant).map(MerchantCommand::getMerchantName).orElse(brandName);

        // 校验账户号
        SettleAccount settleAccount = getSettleAccount(command);
        String accountNo = settleAccount.getAccountNo();

        // 过期时间
        Integer expiryPeriod = Optional.of(command).map(TradePaymentCmd::getExpiryPeriod).orElse(TradeConstant.TRADE_EXPIRY_PERIOD_MAX);
        TradePaymentAttributeDTO attributeDTO = new TradePaymentAttributeDTO();
        attributeDTO.setExpiryPeriod(expiryPeriod);
        log.info("saveTradePayOrder merchantName={}, accountNo={}, attributeDTO={}", merchantName, accountNo, JSONUtil.toJsonStr(attributeDTO));

        // 构建收款提前交易结果
        MoneyCommand money = command.getMoney();
        TradeResultDTO tradeResultDTO = getTradeResultDTO(merchantTradeDTO);
        MerchantResultDTO merchantResult = tradeResultDTO.getMerchantResult();
        BigDecimal merchantFee = feeManager.calculateMerchantFee(money.getAmount(), merchantResult.getSingleRate(), merchantResult.getSingleFee());
        BigDecimal accountAmount = feeManager.calculateAccountAmount(DeductionTypeEnum.DEDUCTION_INTERNAL, money.getAmount(), merchantFee);
        log.info("saveTradePayOrder tradeResultDTO={}", JSONUtil.toJsonStr(tradeResultDTO));

        String orderNo = command.getOrderNo();
        String tradeNo = orderNoManager.getTradeNo(command.getRegion(), TradeTypeEnum.PAYMENT, merchantId);

        // 构建收款订单
        TradePaymentOrder order = new TradePaymentOrder();
        order.setTradeNo(tradeNo);
        order.setOrderNo(orderNo);
        order.setPurpose(command.getPurpose());
        order.setProductDetail(command.getProductDetail());
        order.setPaymentMethod(paymentMethod);
        order.setChannelCode(null);
        order.setChannelCode(null);
        order.setMerchantId(command.getMerchant().getMerchantId());
        order.setMerchantName(merchantName);
        order.setAccountNo(accountNo);

        // 金额, 目前可知的是交易金额，商户分润，商户手续费，商户到账金额
        order.setCurrency(money.getCurrency());
        order.setAmount(money.getAmount());
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
        order.setAttribute(JSONUtil.toJsonStr(attributeDTO));
        order.setSource(command.getTradePaySource().getCode());
        order.setVersion(TradeConstant.INIT_VERSION);
        order.setRegion(command.getRegion());
        order.setCreateTime(LocalDateTime.now());
        tradePaymentOrderRepository.save(order);
        return order;
    }


    /**
     * 构建支付前的交易结果
     */
    private TradeResultDTO getTradeResultDTO(MerchantTradeDTO merchantTradeDTO) {
        TradeResultDTO tradeResultDTO = new TradeResultDTO();

        // 商户配置
        MerchantConfig merchantConfig = Optional.of(merchantTradeDTO)
                .map(MerchantTradeDTO::getMerchantConfig)
                .orElse(new MerchantConfig());
        // 商户收款配置
        MerchantPaymentConfig paymentConfig = Optional.of(merchantTradeDTO)
                .map(MerchantTradeDTO::getMerchantPaymentConfig)
                .orElse(new MerchantPaymentConfig());
        // 商户收款渠道配置
        MerchantPaymentChannelConfig paymentChannelConfig = Optional.of(merchantTradeDTO)
                .map(MerchantTradeDTO::getMerchantPaymentChannelConfig)
                .orElse(new MerchantPaymentChannelConfig());

        // 商户结果
        BigDecimal singleFee = paymentChannelConfig.getSingleFee();
        BigDecimal singleRate = paymentChannelConfig.getSingleRate();

        MerchantResultDTO merchantResult = new MerchantResultDTO();
        merchantResult.setFinishPaymentUrl(merchantConfig.getFinishPaymentUrl());
        merchantResult.setFinishRedirectUrl(merchantConfig.getFinishRedirectUrl());
        merchantResult.setDeductionType(paymentConfig.getDeductionType());
        merchantResult.setSingleFee(singleFee);
        merchantResult.setSingleRate(singleRate);
        merchantResult.setSettleType(paymentChannelConfig.getSettleType());
        merchantResult.setSettleTime(paymentChannelConfig.getSettleTime());

        tradeResultDTO.setMerchantResult(merchantResult);
        tradeResultDTO.setPaymentResult(null);
        tradeResultDTO.setReviewResult(null);
        return tradeResultDTO;
    }

    /**
     * 支付失败返回
     */
    private void handlerFailedTradeResult(TradePaymentOrder order, String error,
                                          UpdateWrapper<TradePaymentOrder> payOrderUpdate) {
        log.info("handlerTradeResult, tradeNo={}, error={}", order.getTradeNo(), error);

        // parse trade result
        TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());
        tradeResultDTO.setSuccess(false);
        tradeResultDTO.setError(error);

        // 更新交易单 交易状态、VA、交易结果、交易下单时间
        payOrderUpdate = Optional.ofNullable(payOrderUpdate).orElse(new UpdateWrapper<>());
        payOrderUpdate.lambda()
                .set(TradePaymentOrder::getTradeTime, LocalDateTime.now()) // 下单时间
                .set(TradePaymentOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                .set(TradePaymentOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                .set(TradePaymentOrder::getPaymentFinishTime, LocalDateTime.now())
                .set(TradePaymentOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePaymentOrder::getId, order.getId());
        tradePaymentOrderRepository.update(payOrderUpdate);

        // 如果是PaymentLink 则更新
        boolean isPaymentLink = Optional.ofNullable(order.getSource())
                .map(TradePaymentSourceEnum::codeToEnum)
                .map(TradePaymentSourceEnum.PAY_LINK::equals)
                .orElse(false);
        if (isPaymentLink) {
            UpdateWrapper<TradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
            linkOrderUpdate.lambda()
                    .set(TradePaymentLinkOrder::getPaymentMethod, order.getPaymentMethod())
                    .set(TradePaymentLinkOrder::getLinkStatus, TradePaymentLinkStatusEnum.PAYMENT_LINK_FAILED.getCode())
                    .eq(TradePaymentLinkOrder::getLinkNo, order.getOrderNo());
            tradePaymentLinkOrderRepository.update(linkOrderUpdate);
        }
    }

    /**
     * 处理支付成功
     */
    private void handlerSuccessTradeResult(BaseTransactionDTO payload,
                                           TradePaymentOrder order) {
        log.info("handlerSuccessTradeResult, tradeNo={}", order.getTradeNo());

        // 先解析原来的交易结果
        TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());
        tradeResultDTO.setSuccess(true);

        // 渠道请求结果
        PaymentResultDTO paymentResultDTO = Optional.of(tradeResultDTO).map(TradeResultDTO::getPaymentResult)
                .orElse(new PaymentResultDTO());
        paymentResultDTO.setChannelOrderNo(payload.getChannelOrderNo());
        paymentResultDTO.setQrString(payload.getChannelQr());
        paymentResultDTO.setPaymentUrl(payload.getChannelPaymentUrl());

        tradeResultDTO.setReviewResult(null);
        tradeResultDTO.setPaymentResult(paymentResultDTO);

        // 更新交易单 交易状态、VA、交易结果、交易下单时间
        UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
        payOrderUpdate.lambda()
                .set(TradePaymentOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(TradePaymentOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))

                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePaymentOrder::getId, order.getId());
        tradePaymentOrderRepository.update(payOrderUpdate);

        // 如果是PaymentLink 则更新
        boolean isPaymentLink = Optional.ofNullable(order.getSource())
                .map(e -> e.equals(TradePaymentSourceEnum.PAY_LINK.getCode()))
                .orElse(false);
        if (isPaymentLink) {
            UpdateWrapper<TradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
            linkOrderUpdate.lambda()
                    .set(TradePaymentLinkOrder::getPaymentMethod, order.getPaymentMethod()) // 要么已存在, 要么已设置
                    .set(TradePaymentLinkOrder::getLinkStatus, TradePaymentLinkStatusEnum.PAYMENT_LINK_PROCESSING.getCode())
                    .eq(TradePaymentLinkOrder::getLinkNo, order.getOrderNo());
            tradePaymentLinkOrderRepository.update(linkOrderUpdate);
        }
    }

    /**
     * 调用外部渠道
     */
    private ChannelResult<? extends BaseTransactionDTO> payPostChannel(TradePaymentOrder order) {
        //路由
        ChannelRouterDTO channelRouterDTO = channelRouterManager.transactionRouter(order);

        PaymentChannel channel = channelRouterDTO.getPaymentChannel();
        PaymentMethod method = channelRouterDTO.getPaymentMethod();
        PaymentChannelMethod channelMethod = channelRouterDTO.getPaymentChannelMethod();
        String channelCode = channel.getChannelCode();

        //执行支付
        //choose method and payment channelMethod then transaction if exception opt order status to fail else ing
        ChannelService channelService = channelServiceDispatcher.getService(ChannelEnum.codeToEnum(channelCode));
        log.info("transaction tradeNo={} channel service={}", order.getTradeNo(), channelService.getChannelName());

        //发起收款
        ChannelResult<? extends BaseTransactionDTO> channelResult = channelService.transaction(channel, method, channelMethod, order);
        log.info("transaction tradeNo={} channel result={}", order.getTradeNo(), JSONUtil.toJsonStr(channelResult));
        return channelResult;
    }

    /**
     * build trade cashier DTO
     */
    private TradeCashierPaymentDTO buildTradeCashierPayDTO(TradePaymentDTO tradePaymentDTO, TradePaymentOrder order,
                                                           TradeCashierStyleDTO styleDTO) {
        TradeCashierPaymentDTO dto = new TradeCashierPaymentDTO();
        dto.setTradeNo(tradePaymentDTO.getTradeNo());
        dto.setTradeTime(LocalDateTime.now().toString());
        dto.setMerchantId(order.getMerchantId());
        dto.setMerchantName(order.getMerchantName());
        dto.setCurrency(tradePaymentDTO.getMoney().getCurrency());
        dto.setAmount(tradePaymentDTO.getMoney().getAmount());
        dto.setPaymentMethod(tradePaymentDTO.getChannel().getPaymentMethod());
        dto.setExpiryPeriod(order.getExpiryPeriod());
        dto.setMethodResult(getMethodResult(order.getTradeResult()));
        dto.setStyle(styleDTO);
        return dto;
    }

    /**
     * 成功订单的结果
     */
    private String getMethodResult(String tradeResult) {
        PaymentResultDTO resultDTO = Optional.ofNullable(tradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getPaymentResult)
                .orElse(null);

        if (Objects.isNull(resultDTO)) {
            return null;
        }

        String qrString = Optional.ofNullable(resultDTO.getQrString()).orElse("");
        String paymentUrl = Optional.ofNullable(resultDTO.getPaymentUrl()).orElse("");
        return qrString + paymentUrl;
    }

    /**
     * 收款补单-成功
     */
    private boolean doPaySupplement(TradePaymentSupplementCmd command) {
        String tradeNo = command.getTradeNo();
        String operator = command.getOperator();
        log.info("doPaySupplement tradeNo={}, operator={}", tradeNo, operator);

        // 查询订单信息
        QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePaymentOrder order = tradePaymentOrderRepository.getOne(payOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FOUND, tradeNo));

        // 如果下单都没成功，谈何补单
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!tradeStatusEnum.equals(TradeStatusEnum.TRADE_SUCCESS)) {
            throw new PaymentException(ExceptionCode.SYSTEM_BUSY, tradeNo);
        }

        // 如果支付状态已经成功, 不需要补单
        // 如果还未支付, 或者支付中 场景是可能上游已经成功但未有回调或回调但未接收到等网络原因等
        // 如果支付状态失败终态, 主动清结算 操作资金 变更为成功： ** 不支持 [成功 -> 失败 -> 成功]，因为有结算记录了。
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_SUCCESS)) {
            throw new PaymentException(ExceptionCode.SYSTEM_BUSY, tradeNo);
        } else {
            // 交易结果
            TradeResultDTO tradeResultDTO = Optional.of(order).map(TradePaymentOrder::getTradeResult)
                    .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                    .orElse(new TradeResultDTO());
            tradeResultDTO.setSuccess(true);

            // 结算补单
            SettleSupplementCmd supplementCmd = new SettleSupplementCmd();
            BeanUtils.copyProperties(order, supplementCmd);
            boolean supplement = settleOrderCmdService.supplement(supplementCmd);
            log.info("doPaySupplement settlePay supplement: {}", supplement);

            PaymentResultAttributeDTO attributeDTO = new PaymentResultAttributeDTO();
            attributeDTO.setType(TradeOrderOptType.SUPPLEMENT.getName());
            attributeDTO.setOperator(operator);

            // 更新订单状态
            UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
            payOrderUpdate.lambda()
                    .set(TradePaymentOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_SUCCESS.getCode())
                    .set(TradePaymentOrder::getPaymentFinishTime, LocalDateTime.now())
                    .set(TradePaymentOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradePaymentOrder::getAttribute, JSONUtil.toJsonStr(attributeDTO))
                    .eq(TradePaymentOrder::getId, order.getId());
            tradePaymentOrderRepository.update(payOrderUpdate);

            // 如果link
            TradePaymentSourceEnum sourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
            if (TradePaymentSourceEnum.PAY_LINK.equals(sourceEnum)) {
                UpdateWrapper<TradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
                linkOrderUpdate.lambda()
                        .set(TradePaymentLinkOrder::getLinkStatus,
                                TradePaymentLinkStatusEnum.PAYMENT_LINK_SUCCESS.getCode())
                        .eq(TradePaymentLinkOrder::getLinkNo, order.getOrderNo());
                tradePaymentLinkOrderRepository.update(linkOrderUpdate);
            }
        }

        return true;
    }

    /**
     * 收款退单（强制设置订单为失败）
     */
    private boolean doPayRefund(TradePaymentRefundCmd command) {
        String tradeNo = command.getTradeNo();
        String operator = command.getOperator();
        log.info("doPayRefund tradeNo={}, operator={}", tradeNo, operator);

        QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePaymentOrder order = tradePaymentOrderRepository.getOne(payOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FOUND, tradeNo));

        // 如果下单都没成功，谈何退单
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!tradeStatusEnum.equals(TradeStatusEnum.TRADE_SUCCESS)) {
            throw new PaymentException(ExceptionCode.SYSTEM_BUSY, tradeNo);
        }

        // 如果还未支付/支付中，直接设置为失败
        // 存在问题：可能渠道的回调消息还未到达，那么直接失败，可能和渠道回调消息的状态不一致
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
        log.info("doPayRefund paymentStatusEnum={}", paymentStatusEnum);

        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_PENDING)
                || paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_PROCESSING)) {
            TradeResultDTO tradeResultDTO = Optional.of(order).map(TradePaymentOrder::getTradeResult)
                    .map(e -> JSONUtil.toBean(e, TradeResultDTO.class)).orElse(new TradeResultDTO());
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError("set refund operate by " + operator);

            PaymentResultAttributeDTO attributeDTO = new PaymentResultAttributeDTO();
            attributeDTO.setType(TradeOrderOptType.REFUND.getName());
            attributeDTO.setOperator(operator);

            // 更新订单状态
            UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
            payOrderUpdate.lambda()
                    .set(TradePaymentOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradePaymentOrder::getPaymentFinishTime, LocalDateTime.now())
                    .set(TradePaymentOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradePaymentOrder::getAttribute, JSONUtil.toJsonStr(attributeDTO))
                    .eq(TradePaymentOrder::getId, order.getId());
            tradePaymentOrderRepository.update(payOrderUpdate);

            // 如果link
            TradePaymentSourceEnum sourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
            if (TradePaymentSourceEnum.PAY_LINK.equals(sourceEnum)) {
                UpdateWrapper<TradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
                linkOrderUpdate.lambda()
                        .set(TradePaymentLinkOrder::getLinkStatus,
                                TradePaymentLinkStatusEnum.PAYMENT_LINK_FAILED.getCode())
                        .eq(TradePaymentLinkOrder::getLinkNo, order.getOrderNo());
                tradePaymentLinkOrderRepository.update(linkOrderUpdate);
            }
        }

        // 如果是支付失败状态，不必进行退单
        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_FAILED)) {
            throw new PaymentException(ExceptionCode.SYSTEM_BUSY, tradeNo);
        }

        // 如果已经是成功状态，强制失败, 账务相关需要回退
        if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_SUCCESS)) {
            SettleRefundCmd refundCmd = new SettleRefundCmd();
            refundCmd.setTradeNo(order.getTradeNo());
            boolean refund = settleOrderCmdService.refund(refundCmd);
            if (Boolean.FALSE.equals(refund)) {
                throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "refund error. tradeNo : " + tradeNo);
            }

            // 更新订单状态
            TradeResultDTO tradeResultDTO = Optional.of(order).map(TradePaymentOrder::getTradeResult)
                    .map(e -> JSONUtil.toBean(e, TradeResultDTO.class)).orElse(new TradeResultDTO());
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError("set refund operate by " + operator);

            PaymentResultAttributeDTO attributeDTO = new PaymentResultAttributeDTO();
            attributeDTO.setType(TradeOrderOptType.REFUND.getName());
            attributeDTO.setOperator(operator);

            UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
            payOrderUpdate.lambda()
                    .set(TradePaymentOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradePaymentOrder::getPaymentFinishTime, LocalDateTime.now())
                    .set(TradePaymentOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradePaymentOrder::getAttribute, JSONUtil.toJsonStr(attributeDTO))
                    .eq(TradePaymentOrder::getId, order.getId());
            tradePaymentOrderRepository.update(payOrderUpdate);

            // 如果link
            TradePaymentSourceEnum sourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
            if (TradePaymentSourceEnum.PAY_LINK.equals(sourceEnum)) {
                UpdateWrapper<TradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
                linkOrderUpdate.lambda()
                        .set(TradePaymentLinkOrder::getLinkStatus,
                                TradePaymentLinkStatusEnum.PAYMENT_LINK_FAILED.getCode())
                        .eq(TradePaymentLinkOrder::getLinkNo, order.getOrderNo());
                tradePaymentLinkOrderRepository.update(linkOrderUpdate);
            }
        }
        return true;
    }

    /**
     * 保存支付链接
     */
    private void saveTradePaymentLinkOrder(TradePaymentCmd command,
                                           String paymentLink) {
        TradePaymentLinkStatusEnum paymentLinkStatusEnum = StringUtils.isBlank(command.getPaymentMethod()) ?
                TradePaymentLinkStatusEnum.PAYMENT_LINK_INIT :
                TradePaymentLinkStatusEnum.PAYMENT_LINK_PROCESSING;
        TradePaymentLinkOrder paymentLinkOrder = new TradePaymentLinkOrder();
        paymentLinkOrder.setLinkNo(command.getOrderNo());
        paymentLinkOrder.setMerchantId(command.getMerchant().getMerchantId());
        paymentLinkOrder.setMerchantName(command.getMerchant().getMerchantName());
        paymentLinkOrder.setPaymentMethod(command.getPaymentMethod());
        paymentLinkOrder.setCurrency(command.getMoney().getCurrency());
        paymentLinkOrder.setAmount(command.getMoney().getAmount());
        paymentLinkOrder.setLinkStatus(paymentLinkStatusEnum.getCode());
        paymentLinkOrder.setNotes(command.getProductDetail());
        paymentLinkOrder.setPaymentLink(paymentLink);
        paymentLinkOrder.setRegion(command.getRegion());

        boolean save = tradePaymentLinkOrderRepository.save(paymentLinkOrder);
        log.info("saveTradePaymentLinkOrder result={}", save);
    }

    /**
     * 获取收银台样式
     */
    private TradeCashierStyleDTO getTradeCashierStyleDTO(TradePaymentOrder order) {
        return null;
    }

}
