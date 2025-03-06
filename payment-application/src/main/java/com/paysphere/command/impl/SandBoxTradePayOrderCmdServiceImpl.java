package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.command.SandBoxTradePayOrderCmdService;
import com.paysphere.command.cmd.ItemDetailCommand;
import com.paysphere.command.cmd.MerchantCommand;
import com.paysphere.command.cmd.MoneyCommand;
import com.paysphere.command.cmd.PayerCommand;
import com.paysphere.command.cmd.ReceiverCommand;
import com.paysphere.command.cmd.SandboxTradeForceSuccessCommand;
import com.paysphere.command.cmd.TradeCashierPaymentCmd;
import com.paysphere.command.cmd.TradeCommand;
import com.paysphere.command.cmd.TradePaymentCmd;
import com.paysphere.command.cmd.TradePaymentLinkCmd;
import com.paysphere.command.dto.TradeCashierPaymentDTO;
import com.paysphere.command.dto.TradeMerchantDTO;
import com.paysphere.command.dto.TradeMoneyDTO;
import com.paysphere.command.dto.TradePayChannelDTO;
import com.paysphere.command.dto.TradePaymentAttributeDTO;
import com.paysphere.command.dto.TradePaymentDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.db.entity.SandboxTradePaymentLinkOrder;
import com.paysphere.db.entity.SandboxTradePaymentOrder;
import com.paysphere.enums.AreaEnum;
import com.paysphere.enums.CallBackStatusEnum;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.TradeModeEnum;
import com.paysphere.enums.TradePaymentLinkStatusEnum;
import com.paysphere.enums.TradePaymentSourceEnum;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.manager.CallbackManager;
import com.paysphere.manager.MerchantManager;
import com.paysphere.manager.OrderNoManager;
import com.paysphere.query.dto.MerchantTradeDTO;
import com.paysphere.repository.SandboxTradePayOrderService;
import com.paysphere.repository.SandboxTradePaymentLinkOrderService;
import com.paysphere.utils.ValidationUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class SandBoxTradePayOrderCmdServiceImpl implements SandBoxTradePayOrderCmdService {

    @Resource
    SandboxTradePayOrderService sandboxTradePayOrderService;
    @Resource
    SandboxTradePaymentLinkOrderService sandboxTradePaymentLinkOrderService;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    MerchantManager merchantManager;
    @Resource
    CallbackManager callbackManager;
    @Resource
    OrderNoManager orderNoManager;

    /**
     * 沙箱 收款API
     */

    @Override
    public TradePaymentDTO executeSandBoxPay(TradePaymentCmd command) {
        log.info("executeSandBoxPay command={}", JSONUtil.toJsonStr(command));
        String orderNo = command.getOrderNo();

        // 判断是否存在，只查询outerNo，不应该存在
        QueryWrapper<SandboxTradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.select("order_no as orderNo");
        payOrderQuery.lambda().eq(SandboxTradePaymentOrder::getOrderNo, orderNo).last(TradeConstant.LIMIT_1);
        SandboxTradePaymentOrder payOrder = sandboxTradePayOrderService.getOne(payOrderQuery);
        Assert.isNull(payOrder, () -> new PaymentException(ExceptionCode.SANDBOX_PAY_ORDER_REPEAT, orderNo));

        // 获取商户配置
        MerchantTradeDTO merchantTradeDTO = null;//merchantManager.getMerchant(command);

        // 如果没有支付方式返回收银台, 有则返回Va等
        return StringUtils.isBlank(command.getPaymentMethod()) ?
                buildSandboxCashier(command, merchantTradeDTO) :
                buildSandboxTradePayDTO(command, merchantTradeDTO);
    }

    /**
     * 沙箱 收银台支付
     */
    @Override
    public TradeCashierPaymentDTO executeSandboxCashierPay(TradeCashierPaymentCmd command) {
        log.info("executeSandboxCashierPay command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();

        // 订单是否存在
        QueryWrapper<SandboxTradePaymentOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SandboxTradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        SandboxTradePaymentOrder order = sandboxTradePayOrderService.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.SANDBOX_PAY_ORDER_NOT_EXIST, tradeNo));

        // 如果已经失败，则异常
        if (order.getTradeStatus().equals(TradeStatusEnum.TRADE_FAILED.getCode())) {
            throw new PaymentException(ExceptionCode.SANDBOX_PAY_ORDER_HAS_FAILED, tradeNo);
        }
        // 校验状态，如果订单已经过期
        if (order.getTradeStatus().equals(TradeStatusEnum.TRADE_EXPIRED.getCode())) {
            throw new PaymentException(ExceptionCode.SANDBOX_PAY_ORDER_HAS_EXPIRED, tradeNo);
        }

        // 再次确认是否过期
        Integer expiryPeriod = Optional.of(order).map(SandboxTradePaymentOrder::getRemark)
                .map(e -> JSONUtil.toBean(e, TradePaymentAttributeDTO.class))
                .map(TradePaymentAttributeDTO::getExpiryPeriod)
                .orElse(TradeConstant.TRADE_EXPIRY_PERIOD_MAX);
        long seconds = Duration.between(order.getTradeTime(), LocalDateTime.now()).getSeconds();
        if (seconds > expiryPeriod) {
            Integer tradeStatus = TradeStatusEnum.TRADE_EXPIRED.getCode();
            UpdateWrapper<SandboxTradePaymentOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(SandboxTradePaymentOrder::getTradeStatus, tradeStatus)
                    .eq(SandboxTradePaymentOrder::getId, order.getId());
            sandboxTradePayOrderService.update(updateWrapper);

            // 订单来源
            TradePaymentSourceEnum tradePaymentSourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
            log.info("paysphere doCashierPay tradePaySourceEnum={}", tradePaymentSourceEnum.name());
            if (TradePaymentSourceEnum.PAY_LINK.equals(tradePaymentSourceEnum)) {
                Integer paymentLinkStatus = TradePaymentLinkStatusEnum.PAYMENT_LINK_EXPIRED.getCode();
                UpdateWrapper<SandboxTradePaymentLinkOrder> linkUpdate = new UpdateWrapper<>();
                linkUpdate.lambda().set(SandboxTradePaymentLinkOrder::getLinkStatus, paymentLinkStatus)
                        .eq(SandboxTradePaymentLinkOrder::getLinkNo, order.getOrderNo());
                sandboxTradePaymentLinkOrderService.update(linkUpdate);
            }
            throw new PaymentException(ExceptionCode.SANDBOX_PAY_ORDER_HAS_EXPIRED, order.getTradeNo());
        }

        // 如果已经成功，则返回, 此处样式使用默认
        if (order.getTradeStatus().equals(TradeStatusEnum.TRADE_SUCCESS.getCode())) {
            TradeCashierPaymentDTO dto = new TradeCashierPaymentDTO();
            dto.setTradeNo(order.getTradeNo());
            dto.setTradeTime(order.getTradeTime().format(TradeConstant.DF_0));
            dto.setMerchantId(order.getMerchantId());
            dto.setMerchantName(order.getMerchantName());
            dto.setCurrency(order.getCurrency());
            dto.setAmount(order.getAmount());
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setExpiryPeriod(getExpiryPeriod(order.getTradeTime()));
            dto.setMethodResult(order.getTradeResultVa());
            return dto;
        }

        // 设置支付方式, 象征意义
        order.setPaymentMethod(command.getPaymentMethod());

        // 设置支付成功
        String va = buildTradeResultVa(order.getPaymentMethod());
        order.setTradeResultVa(va);

        UpdateWrapper<SandboxTradePaymentOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(SandboxTradePaymentOrder::getPaymentMethod, order.getPaymentMethod())
                .set(SandboxTradePaymentOrder::getTradeResultVa, order.getTradeResultVa())
                .set(SandboxTradePaymentOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(SandboxTradePaymentOrder::getMerchantProfit, BigDecimal.ZERO) // 商户分润
                .set(SandboxTradePaymentOrder::getMerchantFee, BigDecimal.ZERO) // 商户手续费
                .set(SandboxTradePaymentOrder::getAccountAmount, order.getAmount()) // 到账金额
                .set(SandboxTradePaymentOrder::getChannelCost, BigDecimal.ZERO) // 通道成本
                .set(SandboxTradePaymentOrder::getPlatformProfit, BigDecimal.ZERO) // 平台利润
                .eq(SandboxTradePaymentOrder::getId, order.getId());
        sandboxTradePayOrderService.update(updateWrapper);

        // 如果是支付链接
        boolean isPaymentLink = Optional.ofNullable(order.getSource())
                .map(e -> e.equals(TradePaymentSourceEnum.PAY_LINK.getCode()))
                .orElse(false);
        if (isPaymentLink) {
            UpdateWrapper<SandboxTradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
            linkOrderUpdate.lambda()
                    .set(SandboxTradePaymentLinkOrder::getLinkStatus, TradePaymentLinkStatusEnum.PAYMENT_LINK_PROCESSING.getCode())
                    .eq(SandboxTradePaymentLinkOrder::getLinkNo, order.getOrderNo());
            sandboxTradePaymentLinkOrderService.update(linkOrderUpdate);
        }

        // 构建收银台返回数据
        return buildTradeCashierPayDTO(order);
    }


    /**
     * 沙箱 强制设置成功失败
     */
    @Override
    public boolean sandboxPayForceSuccessOrFailed(SandboxTradeForceSuccessCommand command) {
        log.info("sandboxPayForceSuccessOrFailed command={}", JSONUtil.toJsonStr(command));

        String tradeNo = command.getTradeNo();

        // 校验订单是否存在
        QueryWrapper<SandboxTradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(SandboxTradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        SandboxTradePaymentOrder order = sandboxTradePayOrderService.getOne(payOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.SANDBOX_PAY_ORDER_NOT_EXIST, tradeNo));

        // 如果下单未成功，则不能强制支付成功、失败
        if (!TradeStatusEnum.TRADE_SUCCESS.getCode().equals(order.getTradeStatus())) {
            throw new PaymentException(ExceptionCode.SANDBOX_PAY_ORDER_TRANSACTION_FAILED, tradeNo);
        }

        // 如果支付状态已经是终态，则异常
        if (PaymentStatusEnum.getFinalStatus().contains(order.getPaymentStatus())) {
            throw new PaymentException(ExceptionCode.SANDBOX_PAY_ORDER_FINAL_STATUS, tradeNo);
        }

        // 支付状态
        PaymentStatusEnum paymentStatus = command.isSuccess()
                ? PaymentStatusEnum.PAYMENT_SUCCESS : PaymentStatusEnum.PAYMENT_FAILED;
        log.info("sandboxPayForceSuccessOrFailed paymentStatus={}", paymentStatus.name());

        UpdateWrapper<SandboxTradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
        payOrderUpdate.lambda().set(SandboxTradePaymentOrder::getPaymentStatus, paymentStatus.getCode())
                .set(SandboxTradePaymentOrder::getPaymentFinishTime, LocalDateTime.now())
                .eq(SandboxTradePaymentOrder::getId, order.getId());
        sandboxTradePayOrderService.update(payOrderUpdate);

        // 如果是 paymentLink
        boolean isPaymentLink = Optional.ofNullable(order.getSource())
                .map(e -> e.equals(TradePaymentSourceEnum.PAY_LINK.getCode()))
                .orElse(false);
        if (isPaymentLink) {
            TradePaymentLinkStatusEnum paymentLinkStatusEnum = command.isSuccess() ?
                    TradePaymentLinkStatusEnum.PAYMENT_LINK_SUCCESS :
                    TradePaymentLinkStatusEnum.PAYMENT_LINK_FAILED;

            UpdateWrapper<SandboxTradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
            linkOrderUpdate.lambda()
                    .set(SandboxTradePaymentLinkOrder::getLinkStatus, paymentLinkStatusEnum.getCode())
                    .eq(SandboxTradePaymentLinkOrder::getLinkNo, order.getOrderNo());
            sandboxTradePaymentLinkOrderService.update(linkOrderUpdate);
        }

        // API订单执行回调，其他不需要 譬如 PayLink. 异步执行
        threadPoolTaskExecutor.execute(() -> sandboxCallback(order, paymentStatus));
        return true;
    }

    /**
     * 沙箱 体验支付链接
     */
    @Override
    public String executeSandboxPaymentLink(TradePaymentLinkCmd command) {
        log.info("executeSandboxPaymentLink command={}", JSONUtil.toJsonStr(command));

        // 构建随机数
        String alphabetic = RandomStringUtils.randomAlphabetic(3);
        String alphanumeric = RandomStringUtils.randomAlphanumeric(3);

        // 先保存sandbox收款
        MoneyCommand moneyCommand = new MoneyCommand();
        moneyCommand.setCurrency(command.getCurrency());
        moneyCommand.setAmount(command.getAmount());

        MerchantCommand merchantCommand = new MerchantCommand();
        merchantCommand.setMerchantId(command.getMerchantId());
        merchantCommand.setMerchantName(command.getMerchantName());

        ItemDetailCommand detailCommand = new ItemDetailCommand();
        detailCommand.setName(alphabetic);
        detailCommand.setQuantity(1);
        detailCommand.setPrice(command.getAmount());

        PayerCommand payer = new PayerCommand();
        payer.setName(alphabetic);
        payer.setEmail(payer.getName() + TradeConstant.PAYMENT_LINK_EMAIL_SUFFIX);
        payer.setPhone(alphanumeric);
        payer.setAddress(alphabetic);

        ReceiverCommand receiver = new ReceiverCommand();
        receiver.setName(alphabetic);
        receiver.setEmail(payer.getName() + TradeConstant.PAYMENT_LINK_EMAIL_SUFFIX);
        receiver.setPhone(alphanumeric);
        receiver.setAddress(alphabetic);

        // 调用支付
        Merchant merchantBaseDTO = new Merchant();
        merchantBaseDTO.setMerchantId(command.getMerchantId());

        String tradeNo = TradeConstant.EXPERIENCE_CASHIER_PREFIX + orderNoManager.getTradeNo(null, TradeTypeEnum.PAYMENT_LINK, merchantBaseDTO.getMerchantId());

        TradePaymentCmd payCommand = new TradePaymentCmd();
        payCommand.setOrderNo(tradeNo);
        payCommand.setPurpose(alphabetic);
        payCommand.setProductDetail(alphabetic);
        payCommand.setMoney(moneyCommand);
        payCommand.setMerchant(merchantCommand);
        payCommand.setPaymentMethod(command.getPaymentMethod());
        payCommand.setPayer(payer);
        payCommand.setReceiver(receiver);
        payCommand.setTradePaySource(TradePaymentSourceEnum.PAY_LINK);
        TradePaymentDTO tradePaymentDTO = this.executeSandBoxPay(payCommand);

        // 收银台地址
        String paymentUrl = Optional.of(tradePaymentDTO).map(TradePaymentDTO::getChannel)
                .map(TradePayChannelDTO::getPaymentUrl)
                .orElse(null);

        // 得到收银台地址，再保存支付链接
        SandboxTradePaymentLinkOrder paymentLinkOrder = new SandboxTradePaymentLinkOrder();
        paymentLinkOrder.setLinkNo(payCommand.getOrderNo());
        paymentLinkOrder.setMerchantId(command.getMerchantId());
        paymentLinkOrder.setMerchantName(command.getMerchantName());
        paymentLinkOrder.setPaymentMethod(command.getPaymentMethod());
        paymentLinkOrder.setCurrency(command.getCurrency());
        paymentLinkOrder.setAmount(command.getAmount());
        paymentLinkOrder.setNotes(command.getNotes());
        paymentLinkOrder.setVersion(TradeConstant.INIT_VERSION);
        paymentLinkOrder.setArea(AreaEnum.INDONESIA.getCode());
        paymentLinkOrder.setPaymentLink(paymentUrl);

        // 如果支付方式空，则初始，否则进行中
        TradePaymentLinkStatusEnum linkStatusEnum = StringUtils.isBlank(command.getPaymentMethod()) ?
                TradePaymentLinkStatusEnum.PAYMENT_LINK_INIT : TradePaymentLinkStatusEnum.PAYMENT_LINK_PROCESSING;
        paymentLinkOrder.setLinkStatus(linkStatusEnum.getCode());
        sandboxTradePaymentLinkOrderService.save(paymentLinkOrder);
        return paymentUrl;
    }

    /**
     * build trade cashier DTO
     */
    private TradeCashierPaymentDTO buildTradeCashierPayDTO(SandboxTradePaymentOrder order) {
        TradeCashierPaymentDTO dto = new TradeCashierPaymentDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setTradeTime(order.getTradeTime().format(TradeConstant.DF_0));
        dto.setMerchantId(order.getMerchantId());
        dto.setMerchantName(order.getMerchantName());
        dto.setCurrency(order.getCurrency());
        dto.setAmount(order.getAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setExpiryPeriod(getExpiryPeriod(order.getTradeTime()));
        dto.setMethodResult(order.getTradeResultVa());
        return dto;
    }


    /**
     * 返回沙箱收银台
     */
    private TradePaymentDTO buildSandboxCashier(TradePaymentCmd command, MerchantTradeDTO merchantTradeDTO) {
        // 保存订单
        SandboxTradePaymentOrder order = saveSandboxTradePayOrder(command, merchantTradeDTO, TradeStatusEnum.TRADE_INIT);
        String transactionTIme = ZonedDateTime.of(order.getTradeTime(), TradeConstant.ZONE_ID).format(TradeConstant.DF_3);

        // 返回收银台
        TradePaymentDTO dto = new TradePaymentDTO();
        dto.setOrderNo(order.getOrderNo());
        dto.setTradeNo(order.getTradeNo());
        dto.setStatus(TradeStatusEnum.TRADE_INIT.name());
        dto.setTransactionTime(transactionTIme);

        TradeMerchantDTO merchant = new TradeMerchantDTO();
        merchant.setMerchantId(order.getMerchantId());
        merchant.setMerchantName(order.getMerchantName());
        merchant.setAccountNo(order.getAccountNo());
        dto.setMerchant(merchant);

        TradeMoneyDTO moneyDTO = new TradeMoneyDTO();
        moneyDTO.setCurrency(order.getCurrency());
        moneyDTO.setAmount(order.getAmount());
        dto.setMoney(moneyDTO);

        TradePayChannelDTO channel = new TradePayChannelDTO();
//        channel.setPaymentUrl(getSandboxPaymentUrl(dto.getTradeNo()));
        dto.setChannel(channel);
        return dto;
    }



    /**
     * 收单交易下单数据入库
     */
    private SandboxTradePaymentOrder saveSandboxTradePayOrder(TradePaymentCmd command,
                                                              MerchantTradeDTO merchantTradeDTO,
                                                              TradeStatusEnum tradeStatusEnum) {
        merchantTradeDTO.getMerchantConfig().setPublicKey("*");
        merchantTradeDTO.getMerchantConfig().setIpWhiteList("*");
        merchantTradeDTO.getMerchantConfig().setMerchantSecret("*");

        Merchant merchantBaseDTO = merchantTradeDTO.getMerchant();
        String merchantName = Optional.of(command).map(TradeCommand::getMerchant)
                .map(MerchantCommand::getMerchantName)
                .orElse(merchantBaseDTO.getMerchantName());
        String tradeNo = TradeConstant.EXPERIENCE_CASHIER_PREFIX + orderNoManager.getTradeNo(null, TradeTypeEnum.PAYMENT, merchantBaseDTO.getMerchantId());

        SandboxTradePaymentOrder order = new SandboxTradePaymentOrder();
        order.setBusinessNo(orderNoManager.getBusinessNo());
        order.setTradeNo(tradeNo);
        order.setOrderNo(command.getOrderNo());

        order.setPurpose(command.getPurpose());
        order.setProductDetail(command.getProductDetail());

        order.setPaymentMethod(command.getPaymentMethod());

        MerchantCommand merchant = command.getMerchant();
        order.setMerchantId(merchant.getMerchantId());
        order.setMerchantName(merchantName);
        order.setAccountNo("000");

        // 沙箱 不收手续费、分润, 实际到账就是收款金额
        MoneyCommand money = command.getMoney();
        order.setCurrency(money.getCurrency());
        order.setAmount(money.getAmount());

        order.setPayerInfo(JSONUtil.toJsonStr(command.getPayer()));
        order.setReceiverInfo(JSONUtil.toJsonStr(command.getReceiver()));

        order.setTradeTime(LocalDateTime.now());
        order.setTradeStatus(tradeStatusEnum.getCode());

        // 如果指定支付方式，则VA就会产生
        if (StringUtils.isNotBlank(command.getPaymentMethod())) {
            order.setTradeResultVa(buildTradeResultVa(command.getPaymentMethod()));
            order.setMerchantProfit(BigDecimal.ZERO); // 商户分润
            order.setMerchantFee(BigDecimal.ZERO); // 商户手续费
            order.setAccountAmount(order.getAmount()); // 收款到帐金额
            order.setChannelCost(BigDecimal.ZERO); // 渠道成本
            order.setPlatformProfit(BigDecimal.ZERO); // 平台利润
        }

        order.setTradeResult(JSONUtil.toJsonStr(merchantTradeDTO));
//        order.setPaymentStatus(PaymentStatusEnum.PAYMENT_UNPAID.getCode());
        order.setCallBackStatus(CallBackStatusEnum.CALLBACK_TODO.getCode());

        order.setSource(command.getTradePaySource().getCode());
        order.setVersion(TradeConstant.INIT_VERSION);
        order.setArea(AreaEnum.INDONESIA.getCode());
        order.setIp("0.0.0.0"); // 需要解析
        order.setCreateTime(LocalDateTime.now());
        boolean save = sandboxTradePayOrderService.save(order);
        Assert.isTrue(save, () -> new PaymentException(ExceptionCode.ORDER_STORAGE_FAILED, order.getOrderNo()));
        return order;
    }

    /**
     * 构建交易结果 FIX 图片切换到storage
     */
    private String buildTradeResultVa(String paymentMethod) {
        if (paymentMethod.startsWith("QRIS") || paymentMethod.startsWith("C_") || paymentMethod.startsWith("S_")) {
            return null;
        } else if (paymentMethod.startsWith("W_")) {
            return paymentMethod.contains("DANA") ? TradeConstant.EXPERIENCE_CASHIER_DANA_URL :
                    TradeConstant.PAYMENT_LINK_REDIRECT;
        } else {
            return RandomStringUtils.randomNumeric(15);
        }
    }

    /**
     * 构建返回数据
     */
    private TradePaymentDTO buildSandboxTradePayDTO(TradePaymentCmd command, MerchantTradeDTO merchantDTO) {
        // 指定某些支付方式
        List<String> paymentMethodList
                = Arrays.asList(
                "BNI", "BJB", "MAYBANK", "MANDIRI", "CIMB", "DANAMON", "PERMATA", "BRI", "BCA",
                "QRIS",
                "W_OVO", "W_DANA", "W_GOPAY", "W_SHOPEEPAY", "W_LINKAJA",
                "C_VISA", "C_MASTER_CARD", "C_JCB",
                "S_ALFAMART", "S_INDOMARET");
        if (!paymentMethodList.contains(command.getPaymentMethod())) {
            log.error("buildSandboxTradePayDTO paymentMethod is invalid. {}", command.getPaymentMethod());
            throw new PaymentException(ExceptionCode.PARAM_IS_INVALID, command.getPaymentMethod());
        }

        // 保存SUCCESS订单
        SandboxTradePaymentOrder order = saveSandboxTradePayOrder(command, merchantDTO, TradeStatusEnum.TRADE_SUCCESS);
        String transactionTIme = ZonedDateTime.of(order.getTradeTime(), TradeConstant.ZONE_ID).format(TradeConstant.DF_3);

        // 返回Va等
        TradePaymentDTO dto = new TradePaymentDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setOrderNo(order.getOrderNo());
//        dto.setStatus(PaymentStatusEnum.codeToEnum(order.getPaymentStatus()).getMerchantStatus());
        dto.setTransactionTime(transactionTIme);

        TradeMerchantDTO merchant = new TradeMerchantDTO();
        merchant.setMerchantId(order.getMerchantId());
        merchant.setMerchantName(order.getMerchantName());
        merchant.setAccountNo(order.getAccountNo());
        dto.setMerchant(merchant);

        TradeMoneyDTO money = new TradeMoneyDTO();
        money.setCurrency(order.getCurrency());
        money.setAmount(order.getAmount());
        dto.setMoney(money);

        TradePayChannelDTO channel = new TradePayChannelDTO();
        channel.setPaymentMethod(order.getPaymentMethod());
        channel.setQrString("To be completed");
//        channel.setPaymentUrl(getSandboxPaymentUrl(dto.getTradeNo()));
        dto.setChannel(channel);
        return dto;
    }

    /**
     * 计算过期时间间隔（秒数）
     */
    private Integer getExpiryPeriod(LocalDateTime tradeTime) {
        if (Objects.isNull(tradeTime)) {
            return 0;
        }
        LocalDateTime expiryTime = tradeTime.plusSeconds(TradeConstant.TRADE_EXPIRY_PERIOD_MAX);
        Duration duration = Duration.between(LocalDateTime.now(), expiryTime);
        long durationSeconds = duration.getSeconds();
        if (durationSeconds <= 0) {
            return 0;
        }
        return Math.toIntExact(durationSeconds);
    }

    /**
     * APi订单进行回调
     */
    @SneakyThrows
    private void sandboxCallback(SandboxTradePaymentOrder order, PaymentStatusEnum paymentStatus) {
        String tradeNo = order.getTradeNo();

        // 执行回调
        TradePaymentSourceEnum sourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
        if (TradePaymentSourceEnum.PAY_LINK.equals(sourceEnum)) {
            log.warn("sandboxCallback tradePaySourceEnum is [PAY_LINK]. No callback");
            return;
        }

        // 回调地址
        String finishPaymentUrl = Optional.of(order).map(SandboxTradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, MerchantTradeDTO.class))
                .map(MerchantTradeDTO::getMerchantConfig)
                .map(MerchantConfig::getFinishPaymentUrl)
                .orElse(null);
        log.info("sandboxCallback tradeNo={} finishPaymentUrl={}", tradeNo, finishPaymentUrl);

        // 如果订单信息中没有, 则从配置在获取一次

        try {
            String merchantId = order.getMerchantId();
            Assert.notBlank(finishPaymentUrl, () -> new PaymentException(ExceptionCode.CALLBACK_URL_NOT_CONFIG, merchantId));

            ZonedDateTime tradeTime = Optional.of(order)
                    .map(SandboxTradePaymentOrder::getTradeTime)
                    .map(e -> e.atZone(TradeConstant.ZONE_ID))
                    .orElse(ZonedDateTime.now());

            TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
            bodyDTO.setTradeNo(tradeNo);
            bodyDTO.setOrderNo(order.getOrderNo());
            bodyDTO.setMerchantId(merchantId);
            bodyDTO.setMerchantName(order.getMerchantName());
            bodyDTO.setTransactionTime(tradeTime.format(TradeConstant.DF_3));
//            bodyDTO.setStatus(paymentStatus.getMerchantStatus());

            TradeCallBackMoneyDTO money = new TradeCallBackMoneyDTO();
            money.setCurrency(order.getCurrency());
            money.setAmount(order.getAmount());
            bodyDTO.setMoney(money);

            TradeCallBackMoneyDTO fee = new TradeCallBackMoneyDTO();
            fee.setCurrency(order.getCurrency());
            fee.setAmount(new BigDecimal(0));
            bodyDTO.setFee(fee);

            TradeCallBackDTO dto = new TradeCallBackDTO();
            dto.setMode(TradeModeEnum.SANDBOX.getMode());
            dto.setUrl(finishPaymentUrl);
            dto.setBody(bodyDTO);

            // 校验参数
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("cash dto validation tradeNo={} errorMsg={}", tradeNo, errorMsg);
                throw new PaymentException(ExceptionCode.CALLBACK_PARAMETER_ERROR, errorMsg);
            }

            // 执行回调
            log.info("sandboxPayCallbackToMerchant tradeNo={} dto={}", tradeNo, JSONUtil.toJsonStr(dto));
            String callback = callbackManager.apiCallback(dto);
            log.info("sandboxPayCallbackToMerchant tradeNo={} result={}", tradeNo, callback);

            // 更新数据
            UpdateWrapper<SandboxTradePaymentOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(SandboxTradePaymentOrder::getCallBackStatus, CallBackStatusEnum.CALLBACK_SUCCESS.getCode())
                    .eq(SandboxTradePaymentOrder::getId, order.getId());
            sandboxTradePayOrderService.update(updateWrapper);
        } catch (Exception e) {
            log.error("sandboxPayCallbackToMerchant tradeNo={} exception", tradeNo, e);
            UpdateWrapper<SandboxTradePaymentOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(SandboxTradePaymentOrder::getCallBackStatus, CallBackStatusEnum.CALLBACK_FAILED.getCode())
                    .eq(SandboxTradePaymentOrder::getId, order.getId());
            sandboxTradePayOrderService.update(updateWrapper);
        }
    }

}
