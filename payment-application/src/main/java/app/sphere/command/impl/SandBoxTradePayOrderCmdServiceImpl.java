package app.sphere.command.impl;

import app.sphere.command.SandBoxTradePayOrderCmdService;
import app.sphere.command.cmd.*;
import app.sphere.command.dto.*;
import app.sphere.command.dto.trade.callback.*;
import app.sphere.manager.CallbackManager;
import app.sphere.manager.OrderNoManager;
import app.sphere.query.dto.MerchantTradeDTO;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.*;
import infrastructure.sphere.db.entity.*;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.*;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import share.sphere.utils.ValidationUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static share.sphere.TradeConstant.LIMIT_1;

/**
 * 沙箱支付订单命令服务实现类
 * 
 * 主要功能：
 * 1. 处理沙箱环境下的支付订单创建和管理
 * 2. 提供收银台支付功能
 * 3. 支持支付链接生成
 * 4. 处理支付回调和状态更新
 * 
 * @author sphere-payment
 */
@Slf4j
@Service
public class SandBoxTradePayOrderCmdServiceImpl implements SandBoxTradePayOrderCmdService {

    @Resource
    SandboxTradePaymentOrderRepository sandboxTradePaymentOrderRepository;
    @Resource
    SandboxTradePaymentLinkOrderRepository sandboxTradePaymentLinkOrderRepository;
    @Resource
    SandboxMerchantConfigRepository sandboxMerchantConfigRepository;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    CallbackManager callbackManager;
    @Resource
    OrderNoManager orderNoManager;

    /**
     * 执行沙箱支付订单创建
     * 
     * @param command 支付命令对象，包含订单基本信息
     * @return TradePaymentDTO 支付结果DTO
     * @throws PaymentException 当订单已存在或商户配置不存在时抛出
     */
    @Override
    public TradePaymentDTO executeSandBoxPay(TradePaymentCmd command) {
        log.info("executeSandBoxPay command={}", JSONUtil.toJsonStr(command));
        String orderNo = command.getOrderNo();
        String merchantId = command.getMerchant().getMerchantId();

        // 判断是否存在，只查询orderNo，不应该存在
        QueryWrapper<SandboxTradePaymentOrder> orderQuery = new QueryWrapper<>();
        orderQuery.select("order_no as orderNo");
        orderQuery.lambda().eq(SandboxTradePaymentOrder::getOrderNo, orderNo).last(LIMIT_1);
        SandboxTradePaymentOrder payOrder = sandboxTradePaymentOrderRepository.getOne(orderQuery);
        Assert.isNull(payOrder, () -> new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FOUND, orderNo));

        // 获取商户沙箱配置
        QueryWrapper<SandboxMerchantConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SandboxMerchantConfig::getMerchantId, merchantId).last(LIMIT_1);
        SandboxMerchantConfig sandboxMerchantConfig = sandboxMerchantConfigRepository.getOne(queryWrapper);
        Assert.notNull(sandboxMerchantConfig, () -> new PaymentException(ExceptionCode.MERCHANT_CONFIG_NOT_EXIST, merchantId));

        //构建基本商户信息
        Merchant merchant = new Merchant();
        merchant.setMerchantId(sandboxMerchantConfig.getMerchantId());
        merchant.setMerchantName(sandboxMerchantConfig.getMerchantName());

        //构建商户沙箱配置
        MerchantConfig configDTO = new MerchantConfig();
        BeanUtils.copyProperties(sandboxMerchantConfig, configDTO);
        MerchantTradeDTO merchantTradeDTO = new MerchantTradeDTO();
        merchantTradeDTO.setMerchant(merchant);
        merchantTradeDTO.setMerchantConfig(configDTO);

        log.info("[沙箱支付] 支付请求处理完成, orderNo={}, paymentMethod={}", orderNo, command.getPaymentMethod());
        return StringUtils.isBlank(command.getPaymentMethod()) ?
                buildSandboxCashier(command, merchantTradeDTO) :
                buildSandboxTradePayDTO(command, merchantTradeDTO);
    }

    /**
     * 执行沙箱收银台支付
     * 
     * @param command 收银台支付命令对象
     * @return TradeCashierPaymentDTO 收银台支付结果DTO
     * @throws PaymentException 当订单不存在、已失败或已过期时抛出
     */
    @Override
    public TradeCashierPaymentDTO executeSandboxCashierPay(TradeCashierPaymentCmd command) {
        log.info("[沙箱收银台] 开始处理收银台支付请求, tradeNo={}", command.getTradeNo());
        String tradeNo = command.getTradeNo();

        // 订单是否存在
        QueryWrapper<SandboxTradePaymentOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SandboxTradePaymentOrder::getTradeNo, tradeNo).last(LIMIT_1);
        SandboxTradePaymentOrder order = sandboxTradePaymentOrderRepository.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FOUND, tradeNo));

        // 如果已经失败，则异常
        if (order.getTradeStatus().equals(TradeStatusEnum.TRADE_FAILED.getCode())) {
            throw new PaymentException(ExceptionCode.TRADE_ORDER_HAS_FAILED, tradeNo);
        }
        // 校验状态，如果订单已经过期
        if (order.getTradeStatus().equals(TradeStatusEnum.TRADE_EXPIRED.getCode())) {
            throw new PaymentException(ExceptionCode.TRADE_ORDER_HAS_EXPIRED, tradeNo);
        }

        // 再次确认是否过期
        Integer expiryPeriod = Optional.of(order).map(SandboxTradePaymentOrder::getAttribute)
                .map(e -> JSONUtil.toBean(e, TradePaymentAttributeDTO.class))
                .map(TradePaymentAttributeDTO::getExpiryPeriod)
                .orElse(TradeConstant.TRADE_EXPIRY_PERIOD_MAX);
        long seconds = System.currentTimeMillis() - order.getTradeTime();
        if (seconds > expiryPeriod) {
            Integer tradeStatus = TradeStatusEnum.TRADE_EXPIRED.getCode();
            UpdateWrapper<SandboxTradePaymentOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(SandboxTradePaymentOrder::getTradeStatus, tradeStatus)
                    .eq(SandboxTradePaymentOrder::getId, order.getId());
            sandboxTradePaymentOrderRepository.update(updateWrapper);

            // 订单来源
            TradePaymentSourceEnum tradePaymentSourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
            log.info("sphere doCashierPay tradePaySourceEnum={}", tradePaymentSourceEnum.name());
            if (TradePaymentSourceEnum.PAY_LINK.equals(tradePaymentSourceEnum)) {
                Integer paymentLinkStatus = TradePaymentLinkStatusEnum.PAYMENT_LINK_EXPIRED.getCode();
                UpdateWrapper<SandboxTradePaymentLinkOrder> linkUpdate = new UpdateWrapper<>();
                linkUpdate.lambda().set(SandboxTradePaymentLinkOrder::getLinkStatus, paymentLinkStatus)
                        .eq(SandboxTradePaymentLinkOrder::getLinkNo, order.getOrderNo());
                sandboxTradePaymentLinkOrderRepository.update(linkUpdate);
            }
            throw new PaymentException(ExceptionCode.TRADE_ORDER_HAS_EXPIRED, order.getTradeNo());
        }

        // 如果已经成功，则返回, 此处样式使用默认
        if (order.getTradeStatus().equals(TradeStatusEnum.TRADE_SUCCESS.getCode())) {
            long time = (System.currentTimeMillis() - order.getTradeTime()) / 1000;
            TradeCashierPaymentDTO dto = new TradeCashierPaymentDTO();
            dto.setTradeNo(order.getTradeNo());
            dto.setTradeTime(orderNoManager.getFormatDateTime(order.getTradeTime(), order.getRegion()));
            dto.setMerchantId(order.getMerchantId());
            dto.setMerchantName(order.getMerchantName());
            dto.setCurrency(order.getCurrency());
            dto.setAmount(order.getAmount());
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setExpiryPeriod(time);
            dto.setMethodResult(null);
            dto.setMethodResultOptType(MethodResultOptType.DISPLAY.name());
            log.info("[沙箱收银台] 收银台支付请求处理完成, tradeNo={}, status={}", tradeNo, order.getTradeStatus());
            return dto;
        }

        // 设置支付方式, 象征意义
        order.setPaymentMethod(command.getPaymentMethod());
        UpdateWrapper<SandboxTradePaymentOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(SandboxTradePaymentOrder::getPaymentMethod, order.getPaymentMethod())
                .set(SandboxTradePaymentOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(SandboxTradePaymentOrder::getMerchantProfit, BigDecimal.ZERO) // 商户分润
                .set(SandboxTradePaymentOrder::getMerchantFee, BigDecimal.ZERO) // 商户手续费
                .set(SandboxTradePaymentOrder::getAccountAmount, order.getAmount()) // 到账金额
                .set(SandboxTradePaymentOrder::getChannelCost, BigDecimal.ZERO) // 通道成本
                .set(SandboxTradePaymentOrder::getPlatformProfit, BigDecimal.ZERO) // 平台利润
                .eq(SandboxTradePaymentOrder::getId, order.getId());
        sandboxTradePaymentOrderRepository.update(updateWrapper);

        // 如果是支付链接
        boolean isPaymentLink = Optional.ofNullable(order.getSource())
                .map(e -> e.equals(TradePaymentSourceEnum.PAY_LINK.getCode()))
                .orElse(false);
        if (isPaymentLink) {
            UpdateWrapper<SandboxTradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
            linkOrderUpdate.lambda()
                    .set(SandboxTradePaymentLinkOrder::getLinkStatus, TradePaymentLinkStatusEnum.PAYMENT_LINK_PROCESSING.getCode())
                    .eq(SandboxTradePaymentLinkOrder::getLinkNo, order.getOrderNo());
            sandboxTradePaymentLinkOrderRepository.update(linkOrderUpdate);
        }

        // 构建收银台返回数据
        return buildTradeCashierPayDTO(order);
    }

    /**
     * 强制设置沙箱支付订单状态
     * 
     * @param command 强制状态设置命令对象
     * @return boolean 操作是否成功
     * @throws PaymentException 当订单不存在或状态不允许修改时抛出
     */
    @Override
    public boolean sandboxPayForceSuccessOrFailed(SandboxTradeForceSuccessCommand command) {
        log.info("[沙箱支付] 开始强制设置订单状态, tradeNo={}, success={}", command.getTradeNo(), command.isSuccess());
        String tradeNo = command.getTradeNo();

        // 校验订单是否存在
        QueryWrapper<SandboxTradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(SandboxTradePaymentOrder::getTradeNo, tradeNo).last(LIMIT_1);
        SandboxTradePaymentOrder order = sandboxTradePaymentOrderRepository.getOne(payOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FOUND, tradeNo));

        // 如果下单未成功，则不能强制支付成功、失败
        if (!TradeStatusEnum.TRADE_SUCCESS.getCode().equals(order.getTradeStatus())) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo);
        }

        // 如果支付状态已经是终态，则异常
        if (PaymentStatusEnum.getFinalStatus().contains(order.getPaymentStatus())) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo);
        }

        // 支付状态
        PaymentStatusEnum paymentStatus = command.isSuccess()
                ? PaymentStatusEnum.PAYMENT_SUCCESS : PaymentStatusEnum.PAYMENT_FAILED;
        log.info("sandboxPayForceSuccessOrFailed paymentStatus={}", paymentStatus.name());

        UpdateWrapper<SandboxTradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
        payOrderUpdate.lambda().set(SandboxTradePaymentOrder::getPaymentStatus, paymentStatus.getCode())
                .set(SandboxTradePaymentOrder::getPaymentFinishTime, LocalDateTime.now())
                .eq(SandboxTradePaymentOrder::getId, order.getId());
        sandboxTradePaymentOrderRepository.update(payOrderUpdate);

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
            sandboxTradePaymentLinkOrderRepository.update(linkOrderUpdate);
        }

        // API订单执行回调，其他不需要 譬如 PayLink. 异步执行
        threadPoolTaskExecutor.execute(() -> sandboxCallback(order, paymentStatus));
        return true;
    }

    /**
     * 执行沙箱支付链接创建
     * 
     * @param command 支付链接命令对象
     * @return String 生成的支付链接编号
     * @throws PaymentException 当参数验证失败或商户配置不存在时抛出
     */
    @Override
    public String executeSandboxPaymentLink(TradePaymentLinkCmd command) {
        log.info("[沙箱支付链接] 开始创建支付链接, merchantId={}", command.getMerchantId());
        
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

        String tradeNo = TradeConstant.EXPERIENCE_CASHIER_PREFIX + orderNoManager.getTradeNo(command.getRegion(), TradeTypeEnum.PAYMENT, merchantBaseDTO.getMerchantId());

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
        paymentLinkOrder.setPaymentLink(paymentUrl);

        // 如果支付方式空，则初始，否则进行中
        TradePaymentLinkStatusEnum linkStatusEnum = StringUtils.isBlank(command.getPaymentMethod()) ?
                TradePaymentLinkStatusEnum.PAYMENT_LINK_INIT : TradePaymentLinkStatusEnum.PAYMENT_LINK_PROCESSING;
        paymentLinkOrder.setLinkStatus(linkStatusEnum.getCode());
        sandboxTradePaymentLinkOrderRepository.save(paymentLinkOrder);
        
        log.info("[沙箱支付链接] 支付链接创建完成, linkNo={}", payCommand.getOrderNo());
        return payCommand.getOrderNo();
    }

    /**
     * 构建收银台支付返回DTO
     * 
     * @param order 沙箱支付订单实体
     * @return TradeCashierPaymentDTO 收银台支付返回DTO
     */
    private TradeCashierPaymentDTO buildTradeCashierPayDTO(SandboxTradePaymentOrder order) {
        log.debug("[沙箱收银台] 开始构建收银台支付返回数据, tradeNo={}", order.getTradeNo());
        
        TradeCashierPaymentDTO dto = new TradeCashierPaymentDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setTradeTime(order.getTradeTime() + "");
        dto.setMerchantId(order.getMerchantId());
        dto.setMerchantName(order.getMerchantName());
        dto.setCurrency(order.getCurrency());
        dto.setAmount(order.getAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setExpiryPeriod(null);
        dto.setMethodResult(null);
        
        log.debug("[沙箱收银台] 收银台支付返回数据构建完成, tradeNo={}", order.getTradeNo());
        return dto;
    }

    /**
     * 构建沙箱收银台支付DTO
     * 
     * @param command 支付命令对象
     * @param merchantTradeDTO 商户交易DTO
     * @return TradePaymentDTO 支付返回DTO
     */
    private TradePaymentDTO buildSandboxCashier(TradePaymentCmd command, MerchantTradeDTO merchantTradeDTO) {
        log.debug("[沙箱支付] 开始构建收银台支付数据, orderNo={}", command.getOrderNo());
        
        // 保存订单
        SandboxTradePaymentOrder order = saveSandboxTradePayOrder(command, merchantTradeDTO, TradeStatusEnum.TRADE_INIT);

        // 返回收银台
        TradePaymentDTO dto = new TradePaymentDTO();
        dto.setOrderNo(order.getOrderNo());
        dto.setTradeNo(order.getTradeNo());
        dto.setStatus(TradeStatusEnum.TRADE_INIT.name());
        dto.setTransactionTime(null);

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
        
        log.debug("[沙箱支付] 收银台支付数据构建完成, orderNo={}", command.getOrderNo());
        return dto;
    }

    /**
     * 保存沙箱支付订单
     * 
     * @param command 支付命令对象
     * @param merchantTradeDTO 商户交易DTO
     * @param tradeStatusEnum 交易状态枚举
     * @return SandboxTradePaymentOrder 保存的沙箱支付订单实体
     */
    private SandboxTradePaymentOrder saveSandboxTradePayOrder(TradePaymentCmd command,
                                                              MerchantTradeDTO merchantTradeDTO,
                                                              TradeStatusEnum tradeStatusEnum) {
        log.debug("[沙箱支付] 开始保存支付订单, orderNo={}", command.getOrderNo());
        
        merchantTradeDTO.getMerchantConfig().setPublicKey("*");
        merchantTradeDTO.getMerchantConfig().setIpWhiteList("*");
        merchantTradeDTO.getMerchantConfig().setMerchantSecret("*");

        Merchant merchantBaseDTO = merchantTradeDTO.getMerchant();
        String merchantName = Optional.of(command).map(TradeCommand::getMerchant)
                .map(MerchantCommand::getMerchantName)
                .orElse(merchantBaseDTO.getMerchantName());
        String tradeNo = TradeConstant.EXPERIENCE_CASHIER_PREFIX + orderNoManager.getTradeNo(command.getRegion(), TradeTypeEnum.PAYMENT, merchantBaseDTO.getMerchantId());

        SandboxTradePaymentOrder order = new SandboxTradePaymentOrder();
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

        order.setTradeTime(System.currentTimeMillis());
        order.setTradeStatus(tradeStatusEnum.getCode());

        // 如果指定支付方式，则VA就会产生
        if (StringUtils.isNotBlank(command.getPaymentMethod())) {
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
        order.setCreateTime(LocalDateTime.now());
        boolean save = sandboxTradePaymentOrderRepository.save(order);
        Assert.isTrue(save, () -> new PaymentException(ExceptionCode.SYSTEM_BUSY, order.getOrderNo()));
        
        log.debug("[沙箱支付] 支付订单保存完成, orderNo={}, tradeNo={}", command.getOrderNo(), order.getTradeNo());
        return order;
    }

    /**
     * 构建沙箱支付返回DTO
     * 
     * @param command 支付命令对象
     * @param merchantDTO 商户DTO
     * @return TradePaymentDTO 支付返回DTO
     */
    private TradePaymentDTO buildSandboxTradePayDTO(TradePaymentCmd command, MerchantTradeDTO merchantDTO) {
        log.debug("[沙箱支付] 开始构建支付返回数据, orderNo={}", command.getOrderNo());
        
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
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, command.getPaymentMethod());
        }

        // 保存SUCCESS订单
        SandboxTradePaymentOrder order = saveSandboxTradePayOrder(command, merchantDTO, TradeStatusEnum.TRADE_SUCCESS);

        // 返回Va等
        TradePaymentDTO dto = new TradePaymentDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setOrderNo(order.getOrderNo());
//        dto.setStatus(PaymentStatusEnum.codeToEnum(order.getPaymentStatus()).getMerchantStatus());
        dto.setTransactionTime(null);

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
        
        log.debug("[沙箱支付] 支付返回数据构建完成, orderNo={}", command.getOrderNo());
        return dto;
    }

    /**
     * 执行沙箱支付回调
     * 异步处理支付结果回调通知
     * 
     * @param order 沙箱支付订单实体
     * @param paymentStatus 支付状态枚举
     */
    @SneakyThrows
    private void sandboxCallback(SandboxTradePaymentOrder order, PaymentStatusEnum paymentStatus) {
        log.info("[沙箱支付回调] 开始处理支付回调, tradeNo={}, status={}", order.getTradeNo(), paymentStatus.name());
        
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
            Assert.notBlank(finishPaymentUrl, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, merchantId));


            TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
            bodyDTO.setTradeNo(tradeNo);
            bodyDTO.setOrderNo(order.getOrderNo());
            bodyDTO.setMerchantId(merchantId);
            bodyDTO.setMerchantName(order.getMerchantName());
            bodyDTO.setTransactionTime(null);
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
                throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, errorMsg);
            }

            // 执行回调
            log.info("sandboxPayCallbackToMerchant tradeNo={} dto={}", tradeNo, JSONUtil.toJsonStr(dto));
            String callback = callbackManager.apiCallback(dto);
            log.info("sandboxPayCallbackToMerchant tradeNo={} result={}", tradeNo, callback);

            // 更新数据
            UpdateWrapper<SandboxTradePaymentOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(SandboxTradePaymentOrder::getCallBackStatus, CallBackStatusEnum.CALLBACK_SUCCESS.getCode())
                    .eq(SandboxTradePaymentOrder::getId, order.getId());
            sandboxTradePaymentOrderRepository.update(updateWrapper);
        } catch (Exception e) {
            log.error("sandboxPayCallbackToMerchant tradeNo={} exception", tradeNo, e);
            UpdateWrapper<SandboxTradePaymentOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(SandboxTradePaymentOrder::getCallBackStatus, CallBackStatusEnum.CALLBACK_FAILED.getCode())
                    .eq(SandboxTradePaymentOrder::getId, order.getId());
            sandboxTradePaymentOrderRepository.update(updateWrapper);
        }
        
        log.info("[沙箱支付回调] 支付回调处理完成, tradeNo={}, callbackStatus={}", order.getTradeNo(), order.getCallBackStatus());
    }

}
