package app.sphere.command.impl;

import app.sphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.SandboxMerchantConfigRepository;
import infrastructure.sphere.db.entity.SandboxMerchantConfig;
import org.springframework.beans.BeanUtils;
import share.sphere.TradeConstant;
import app.sphere.command.SandBoxTradeCashOrderCmdService;
import app.sphere.command.cmd.MerchantCommand;
import app.sphere.command.cmd.MoneyCommand;
import app.sphere.command.cmd.SandboxTradeForceSuccessCommand;
import app.sphere.command.cmd.TradeCommand;
import app.sphere.command.cmd.TradePayoutCommand;
import app.sphere.command.dto.TradeMerchantDTO;
import app.sphere.command.dto.TradeMoneyDTO;
import app.sphere.command.dto.TradePayoutChannelDTO;
import app.sphere.command.dto.TradePayoutDTO;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantConfig;
import infrastructure.sphere.db.entity.SandboxTradePayoutOrder;
import share.sphere.enums.CallBackStatusEnum;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.enums.TradeModeEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import app.sphere.manager.CallbackManager;
import app.sphere.manager.OrderNoManager;
import app.sphere.query.dto.MerchantTradeDTO;
import domain.sphere.repository.SandboxTradePayoutOrderRepository;
import share.sphere.utils.ValidationUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static share.sphere.TradeConstant.LIMIT_1;

/**
 * 沙箱代付订单命令服务实现类
 * 
 * 主要功能：
 * 1. 处理沙箱环境下的代付订单创建和管理
 * 2. 提供代付订单状态强制设置功能
 * 3. 处理代付回调通知
 * 
 * @author sphere-payment
 */
@Slf4j
@Service
public class SandBoxTradePayoutOrderCmdServiceImpl implements SandBoxTradeCashOrderCmdService {

    @Resource
    SandboxTradePayoutOrderRepository sandboxTradePayoutOrderRepository;
    @Resource
    SandboxMerchantConfigRepository sandboxMerchantConfigRepository;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    OrderNoManager orderNoManager;
    @Resource
    CallbackManager callbackManager;

    /**
     * 执行沙箱代付订单创建
     * 
     * @param command 代付命令对象，包含订单基本信息
     * @return TradePayoutDTO 代付结果DTO
     * @throws PaymentException 当订单已存在或商户配置不存在时抛出
     */
    @Override
    public TradePayoutDTO executeSandboxCash(TradePayoutCommand command) {
        log.info("[沙箱代付] 开始处理代付请求, orderNo={}, merchantId={}", command.getOrderNo(), command.getMerchant().getMerchantId());
        String orderNo = command.getOrderNo();
        String merchantId = command.getMerchant().getMerchantId();

        // 校验外部单号, 只查询orderNo
        QueryWrapper<SandboxTradePayoutOrder> orderQuery = new QueryWrapper<>();
        orderQuery.select("order_no as orderNo");
        orderQuery.lambda().eq(SandboxTradePayoutOrder::getOrderNo, orderNo).last(TradeConstant.LIMIT_1);
        SandboxTradePayoutOrder cashOrder = sandboxTradePayoutOrderRepository.getOne(orderQuery);
        Assert.isNull(cashOrder, () -> new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FOUND, orderNo));

        // 校验商户配置 & 保存订单信息
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

        SandboxTradePayoutOrder order = saveSandboxTradeCashOrder(command, merchantTradeDTO);

        log.info("[沙箱代付] 代付请求处理完成, orderNo={}, tradeNo={}", orderNo, order.getTradeNo());
        return buildTradeCashDTO(order);
    }

    /**
     * 强制设置沙箱代付订单状态
     * 
     * @param command 强制状态设置命令对象
     * @return boolean 操作是否成功
     * @throws PaymentException 当订单不存在或状态不允许修改时抛出
     */
    @Override
    public boolean sandboxCashForceSuccessOrFailed(SandboxTradeForceSuccessCommand command) {
        log.info("[沙箱代付] 开始强制设置订单状态, tradeNo={}, success={}", command.getTradeNo(), command.isSuccess());
        String tradeNo = command.getTradeNo();

        // 校验订单是否存在
        QueryWrapper<SandboxTradePayoutOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SandboxTradePayoutOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        SandboxTradePayoutOrder order = sandboxTradePayoutOrderRepository.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FOUND, tradeNo));

        // 如果下单未成功，则不能强制支付成功、失败
        if (!TradeStatusEnum.TRADE_SUCCESS.getCode().equals(order.getTradeStatus())) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo);
        }

        // 如果支付状态已经是终态，则异常
        if (PaymentStatusEnum.getFinalStatus().contains(order.getPaymentStatus())) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo);
        }

        PaymentStatusEnum paymentStatus = command.isSuccess()
                ? PaymentStatusEnum.PAYMENT_SUCCESS : PaymentStatusEnum.PAYMENT_FAILED;
        log.info("sandboxCashForceSuccessOrFailed paymentStatus={}", paymentStatus.name());

        UpdateWrapper<SandboxTradePayoutOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(SandboxTradePayoutOrder::getPaymentStatus, paymentStatus.getCode())
                .set(SandboxTradePayoutOrder::getPaymentFinishTime, LocalDateTime.now())
                .eq(SandboxTradePayoutOrder::getId, order.getId());
        sandboxTradePayoutOrderRepository.update(updateWrapper);

        threadPoolTaskExecutor.execute(() -> sandboxCallback(order, paymentStatus));
        log.info("[沙箱代付] 订单状态强制设置完成, tradeNo={}, status={}", tradeNo, paymentStatus.name());
        return true;
    }

    // -------------------------------------------------------------------------------------------------------------

    /**
     * 构建代付返回DTO
     * 
     * @param order 沙箱代付订单实体
     * @return TradePayoutDTO 代付返回DTO
     */
    private TradePayoutDTO buildTradeCashDTO(SandboxTradePayoutOrder order) {
        log.debug("[沙箱代付] 开始构建代付返回数据, tradeNo={}", order.getTradeNo());
        
        TradePayoutDTO dto = new TradePayoutDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setStatus(PaymentStatusEnum.codeToEnum(order.getPaymentStatus()).name());
        dto.setDisbursementTime(null);

        TradeMerchantDTO merchant = new TradeMerchantDTO();
        merchant.setMerchantId(order.getMerchantId());
        merchant.setMerchantName(order.getMerchantName());
        merchant.setAccountNo(order.getAccountNo());

        TradePayoutChannelDTO channel = new TradePayoutChannelDTO();
        channel.setPaymentMethod(order.getPaymentMethod());
        channel.setBankAccount(order.getBankAccount());

        TradeMoneyDTO money = new TradeMoneyDTO();
        money.setCurrency(order.getCurrency());
        money.setAmount(order.getAmount());

        dto.setMerchant(merchant);
        dto.setChannel(channel);
        dto.setMoney(money);

        log.debug("[沙箱代付] 代付返回数据构建完成, tradeNo={}", order.getTradeNo());
        return dto;
    }

    /**
     * 保存沙箱代付订单
     * 
     * @param command 代付命令对象
     * @param merchantTradeDTO 商户交易DTO
     * @return SandboxTradePayoutOrder 保存的沙箱代付订单实体
     */
    private SandboxTradePayoutOrder saveSandboxTradeCashOrder(TradePayoutCommand command, MerchantTradeDTO merchantTradeDTO) {
        log.debug("[沙箱代付] 开始保存代付订单, orderNo={}", command.getOrderNo());
        
        merchantTradeDTO.getMerchantConfig().setPublicKey("*");
        merchantTradeDTO.getMerchantConfig().setIpWhiteList("*");
        merchantTradeDTO.getMerchantConfig().setMerchantSecret("*");

        Merchant merchantBaseDTO = merchantTradeDTO.getMerchant();
        String merchantName = Optional.of(command).map(TradeCommand::getMerchant)
                .map(MerchantCommand::getMerchantName)
                .orElse(merchantBaseDTO.getMerchantName());
        String tradeNo = TradeConstant.EXPERIENCE_CASHIER_PREFIX + orderNoManager.getTradeNo(command.getRegion(), TradeTypeEnum.PAYOUT, merchantBaseDTO.getMerchantId());

        SandboxTradePayoutOrder order = new SandboxTradePayoutOrder();
        order.setTradeNo(tradeNo);
        order.setOrderNo(command.getOrderNo());
        order.setPurpose(command.getPurpose());
        order.setProductDetail(command.getProductDetail());
        order.setPaymentMethod(command.getPaymentMethod());
        order.setBankAccount(command.getBankAccount());

        MerchantCommand merchant = command.getMerchant();
        order.setMerchantId(merchant.getMerchantId());
        order.setMerchantName(merchantName);
        order.setAccountNo("000");

        // 交易金额 实扣金额 手续费 到账金额
        MoneyCommand money = command.getMoney();
        order.setCurrency(money.getCurrency());
        order.setAmount(money.getAmount()); // 交易金额
        order.setActualAmount(order.getAmount()); // 实扣金额
        order.setMerchantFee(BigDecimal.ZERO); // 商户手续费
        order.setAccountAmount(order.getAmount()); // 到账金额
        order.setMerchantProfit(BigDecimal.ZERO); // 商户分润
        order.setChannelCost(BigDecimal.ZERO); // 通道成本
        order.setPlatformProfit(BigDecimal.ZERO); // 平台利润

        order.setPayerInfo(JSONUtil.toJsonStr(command.getPayer()));
        order.setReceiverInfo(JSONUtil.toJsonStr(command.getReceiver()));

        order.setTradeTime(System.currentTimeMillis());
        order.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        order.setTradeResult(JSONUtil.toJsonStr(merchantTradeDTO));
        order.setPaymentStatus(PaymentStatusEnum.PAYMENT_PENDING.getCode());
        order.setCallBackStatus(CallBackStatusEnum.CALLBACK_TODO.getCode());
        order.setVersion(TradeConstant.INIT_VERSION);
        order.setRegion(command.getRegion());
        order.setCreateTime(LocalDateTime.now());
        sandboxTradePayoutOrderRepository.save(order);
        log.debug("[沙箱代付] 代付订单保存完成, orderNo={}, tradeNo={}", command.getOrderNo(), order.getTradeNo());
        return order;
    }

    /**
     * 执行沙箱代付回调
     * 异步处理代付结果回调通知
     * 
     * @param order 沙箱代付订单实体
     * @param paymentStatus 支付状态枚举
     */
    private void sandboxCallback(SandboxTradePayoutOrder order, PaymentStatusEnum paymentStatus) {
        log.info("[沙箱代付回调] 开始处理代付回调, tradeNo={}, status={}", order.getTradeNo(), paymentStatus.name());
        
        String tradeNo = order.getTradeNo();

        // 回调地址
        String finishCashUrl = Optional.of(order).map(SandboxTradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, MerchantTradeDTO.class))
                .map(MerchantTradeDTO::getMerchantConfig)
                .map(MerchantConfig::getFinishPayoutUrl)
                .orElse(null);
        log.info("sandboxCallback tradeNo={} finishCashUrl={}", tradeNo, finishCashUrl);

        // TODO 如果订单信息中没有, 则从配置在获取一次

        try {
            String merchantId = order.getMerchantId();
            Assert.notBlank(finishCashUrl, () -> new PaymentException(ExceptionCode.SYSTEM_BUSY, merchantId));


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
            fee.setAmount(order.getMerchantFee());
            bodyDTO.setFee(fee);

            TradeCallBackDTO dto = new TradeCallBackDTO();
            dto.setMode(TradeModeEnum.SANDBOX.getMode());
            dto.setUrl(finishCashUrl);
            dto.setBody(bodyDTO);

            // 校验参数
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("sphere sandboxCallback validation tradeNo={} errorMsg={}", tradeNo, errorMsg);
                throw new PaymentException(ExceptionCode.SYSTEM_BUSY, errorMsg);
            }

            // 执行回调
            log.info("sandboxCallback tradeNo={} dto={}", tradeNo, JSONUtil.toJsonStr(dto));
            String callback = callbackManager.apiCallback(dto);
            log.info("sandboxCallback tradeNo={} result={}", tradeNo, callback);

            // 更新数据
            UpdateWrapper<SandboxTradePayoutOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(SandboxTradePayoutOrder::getCallBackStatus, CallBackStatusEnum.CALLBACK_SUCCESS.getCode())
                    .eq(SandboxTradePayoutOrder::getId, order.getId());
            sandboxTradePayoutOrderRepository.update(updateWrapper);
        } catch (Exception e) {

            log.error("sandboxCallback tradeNo={} exception", tradeNo, e);
            UpdateWrapper<SandboxTradePayoutOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(SandboxTradePayoutOrder::getCallBackStatus, CallBackStatusEnum.CALLBACK_FAILED.getCode())
                    .eq(SandboxTradePayoutOrder::getId, order.getId());
            sandboxTradePayoutOrderRepository.update(updateWrapper);
        }
        
        log.info("[沙箱代付回调] 代付回调处理完成, tradeNo={}, callbackStatus={}", order.getTradeNo(), order.getCallBackStatus());
    }

}
