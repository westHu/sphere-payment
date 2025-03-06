package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.command.SandBoxTradeCashOrderCmdService;
import com.paysphere.command.cmd.MerchantCommand;
import com.paysphere.command.cmd.MoneyCommand;
import com.paysphere.command.cmd.SandboxTradeForceSuccessCommand;
import com.paysphere.command.cmd.TradeCashCommand;
import com.paysphere.command.cmd.TradeCommand;
import com.paysphere.command.dto.TradeMerchantDTO;
import com.paysphere.command.dto.TradeMoneyDTO;
import com.paysphere.command.dto.TradePayoutChannelDTO;
import com.paysphere.command.dto.TradePayoutDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.db.entity.SandboxTradePayoutOrder;
import com.paysphere.enums.CallBackStatusEnum;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.TradeModeEnum;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.manager.CallbackManager;
import com.paysphere.manager.OrderNoManager;
import com.paysphere.query.dto.MerchantTradeDTO;
import com.paysphere.repository.SandboxTradeCashOrderService;
import com.paysphere.utils.ValidationUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;


@Slf4j
@Service
public class SandBoxTradeCashOrderCmdServiceImpl implements SandBoxTradeCashOrderCmdService {

    @Resource
    SandboxTradeCashOrderService sandboxTradeCashOrderService;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    OrderNoManager orderNoManager;
    @Resource
    CallbackManager callbackManager;

    /**
     * 沙箱代付
     */
    @Override
    public TradePayoutDTO executeSandboxCash(TradeCashCommand command) {
        String orderNo = command.getOrderNo();

        // 校验外部单号, 只查询outerNo
        QueryWrapper<SandboxTradePayoutOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("order_no as orderNo");
        queryWrapper.lambda().eq(SandboxTradePayoutOrder::getOrderNo, orderNo).last(TradeConstant.LIMIT_1);
        SandboxTradePayoutOrder cashOrder = sandboxTradeCashOrderService.getOne(queryWrapper);
        Assert.isNull(cashOrder, () -> new PaymentException(ExceptionCode.SANDBOX_CASH_ORDER_REPEAT, orderNo));

        // 校验商户配置 & 保存订单信息
        MerchantTradeDTO merchantDTO = getSandboxMerchant(command);
        SandboxTradePayoutOrder order = saveSandboxTradeCashOrder(command, merchantDTO);

        // 构建返回数据
        return buildTradeCashDTO(order);
    }


    /**
     * 沙箱代付 - 设置成功/失败 - 回调
     */
    @Override
    public boolean sandboxCashForceSuccessOrFailed(SandboxTradeForceSuccessCommand command) {
        String tradeNo = command.getTradeNo();

        // 校验订单是否存在
        QueryWrapper<SandboxTradePayoutOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SandboxTradePayoutOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        SandboxTradePayoutOrder order = sandboxTradeCashOrderService.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.SANDBOX_CASH_ORDER_NOT_EXIST, tradeNo));

        // 如果下单未成功，则不能强制支付成功、失败
        if (!TradeStatusEnum.TRADE_SUCCESS.getCode().equals(order.getTradeStatus())) {
            throw new PaymentException(ExceptionCode.SANDBOX_CASH_ORDER_TRANSACTION_FAILED, tradeNo);
        }

        // 如果支付状态已经是终态，则异常
        if (PaymentStatusEnum.getFinalStatus().contains(order.getPaymentStatus())) {
            throw new PaymentException(ExceptionCode.SANDBOX_CASH_ORDER_FINAL_STATUS, tradeNo);
        }

        PaymentStatusEnum paymentStatus = command.isSuccess()
                ? PaymentStatusEnum.PAYMENT_SUCCESS : PaymentStatusEnum.PAYMENT_FAILED;
        log.info("sandboxCashForceSuccessOrFailed paymentStatus={}", paymentStatus.name());

        UpdateWrapper<SandboxTradePayoutOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(SandboxTradePayoutOrder::getPaymentStatus, paymentStatus.getCode())
                .set(SandboxTradePayoutOrder::getPaymentFinishTime, LocalDateTime.now())
                .eq(SandboxTradePayoutOrder::getId, order.getId());
        sandboxTradeCashOrderService.update(updateWrapper);

        threadPoolTaskExecutor.execute(() -> sandboxCallback(order, paymentStatus));
        return true;
    }


    // -------------------------------------------------------------------------------------------------------------

    /**
     * 沙箱校验并得到商户信息
     */
    private MerchantTradeDTO getSandboxMerchant(TradeCashCommand command) {
        String merchantId = command.getMerchant().getMerchantId();


        MerchantTradeDTO merchantDTO = new MerchantTradeDTO();
        merchantDTO.setMerchant(null);
        merchantDTO.setMerchantConfig(null);
        return merchantDTO;
    }


    /**
     * 沙箱 代付构建返回数据
     */
    private TradePayoutDTO buildTradeCashDTO(SandboxTradePayoutOrder order) {
        String disbursementTime = ZonedDateTime.of(order.getTradeTime(), TradeConstant.ZONE_ID).format(TradeConstant.DF_3);

        TradePayoutDTO dto = new TradePayoutDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setOuterNo(order.getOrderNo());
//        dto.setStatus(PaymentStatusEnum.codeToEnum(order.getPaymentStatus()).getMerchantStatus());
        dto.setDisbursementTime(disbursementTime);

        TradeMerchantDTO merchant = new TradeMerchantDTO();
        merchant.setMerchantId(order.getMerchantId());
        merchant.setMerchantName(order.getMerchantName());
        merchant.setAccountNo(order.getAccountNo());

        TradePayoutChannelDTO channel = new TradePayoutChannelDTO();
        channel.setPaymentMethod(order.getPaymentMethod());
        channel.setCashAccount(order.getCashAccount());

        TradeMoneyDTO money = new TradeMoneyDTO();
        money.setCurrency(order.getCurrency());
        money.setAmount(order.getAmount());

        dto.setMerchant(merchant);
        dto.setChannel(channel);
        dto.setMoney(money);

        return dto;
    }


    /**
     * 沙箱代付下单
     */
    private SandboxTradePayoutOrder saveSandboxTradeCashOrder(TradeCashCommand command, MerchantTradeDTO merchantTradeDTO) {
        merchantTradeDTO.getMerchantConfig().setPublicKey("*");
        merchantTradeDTO.getMerchantConfig().setIpWhiteList("*");
        merchantTradeDTO.getMerchantConfig().setMerchantSecret("*");

        Merchant merchantBaseDTO = merchantTradeDTO.getMerchant();
        String merchantName = Optional.of(command).map(TradeCommand::getMerchant)
                .map(MerchantCommand::getMerchantName)
                .orElse(merchantBaseDTO.getMerchantName());
        String tradeNo = TradeConstant.EXPERIENCE_CASHIER_PREFIX + orderNoManager.getTradeNo(null, TradeTypeEnum.PAYOUT, merchantBaseDTO.getMerchantId());

        SandboxTradePayoutOrder order = new SandboxTradePayoutOrder();
        order.setBusinessNo(orderNoManager.getBusinessNo());
        order.setTradeNo(tradeNo);
        order.setOrderNo(command.getOrderNo());
        order.setPurpose(command.getPurpose());
        order.setProductDetail(command.getProductDetail());
        order.setPaymentMethod(command.getPaymentMethod());
        order.setCashAccount(command.getCashAccount());

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

        order.setTradeTime(LocalDateTime.now());
        order.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        order.setTradeResult(JSONUtil.toJsonStr(merchantTradeDTO));
//        order.setPaymentStatus(PaymentStatusEnum.PAYMENT_UNPAID.getCode());
        order.setCallBackStatus(CallBackStatusEnum.CALLBACK_TODO.getCode());
        order.setCallBackTimes(0);
        order.setVersion(TradeConstant.INIT_VERSION);
        order.setArea(merchantBaseDTO.getArea());
        order.setIp("0.0.0.0"); // 解析IP
        order.setCreateTime(LocalDateTime.now());
        sandboxTradeCashOrderService.save(order);
        return order;
    }


    /**
     * api执行回调
     */
    private void sandboxCallback(SandboxTradePayoutOrder order, PaymentStatusEnum paymentStatus) {
        String tradeNo = order.getTradeNo();

        // 回调地址
        String finishCashUrl = Optional.of(order).map(SandboxTradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, MerchantTradeDTO.class))
                .map(MerchantTradeDTO::getMerchantConfig)
                .map(MerchantConfig::getFinishCashUrl)
                .orElse(null);
        log.info("sandboxCallback tradeNo={} finishCashUrl={}", tradeNo, finishCashUrl);

        // TODO 如果订单信息中没有, 则从配置在获取一次

        try {
            String merchantId = order.getMerchantId();
            Assert.notBlank(finishCashUrl, () -> new PaymentException(ExceptionCode.CALLBACK_URL_NOT_CONFIG, merchantId));

            ZonedDateTime tradeTime = Optional.of(order).map(SandboxTradePayoutOrder::getTradeTime)
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
            fee.setAmount(order.getMerchantFee());
            bodyDTO.setFee(fee);

            TradeCallBackDTO dto = new TradeCallBackDTO();
            dto.setMode(TradeModeEnum.SANDBOX.getMode());
            dto.setUrl(finishCashUrl);
            dto.setBody(bodyDTO);

            // 校验参数
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("paysphere sandboxCallback validation tradeNo={} errorMsg={}", tradeNo, errorMsg);
                throw new PaymentException(ExceptionCode.CALLBACK_PARAMETER_ERROR, errorMsg);
            }

            // 执行回调
            log.info("sandboxCallback tradeNo={} dto={}", tradeNo, JSONUtil.toJsonStr(dto));
            String callback = callbackManager.apiCallback(dto);
            log.info("sandboxCallback tradeNo={} result={}", tradeNo, callback);

            // 更新数据
            UpdateWrapper<SandboxTradePayoutOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(SandboxTradePayoutOrder::getCallBackStatus, CallBackStatusEnum.CALLBACK_SUCCESS.getCode())
                    .set(SandboxTradePayoutOrder::getCallBackTimes, 1)
                    .eq(SandboxTradePayoutOrder::getId, order.getId());
            sandboxTradeCashOrderService.update(updateWrapper);
        } catch (Exception e) {

            log.error("sandboxCallback tradeNo={} exception", tradeNo, e);
            UpdateWrapper<SandboxTradePayoutOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(SandboxTradePayoutOrder::getCallBackStatus, CallBackStatusEnum.CALLBACK_FAILED.getCode())
                    .set(SandboxTradePayoutOrder::getCallBackTimes, 1)
                    .eq(SandboxTradePayoutOrder::getId, order.getId());
            sandboxTradeCashOrderService.update(updateWrapper);
        }
    }

}
