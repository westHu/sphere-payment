package com.paysphere.manager;


import com.paysphere.enums.DeductionTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;


/**
 * @Author west
 **/
@Slf4j
@Component
public class FeeManager {

//    @Resource
//    RocketMqProducer rocketMqProducer;

    /**
     * 计算商户手续费
     */
    public BigDecimal calculateMerchantFee(BigDecimal amount,
                                           BigDecimal singleRate,
                                           BigDecimal singleFee) {
        BigDecimal merchantFee = amount.multiply(singleRate).add(singleFee);
        log.info("calculateMerchantFee merchantFee={}",  merchantFee);
        return merchantFee;
    }

    /**
     * 计算实际出款额 - 代付提现
     */
    public BigDecimal calculateActualAmount(DeductionTypeEnum deductionTypeEnum,
                                            BigDecimal amount,
                                            BigDecimal merchantFee) {
        BigDecimal actualAmount;
        if (DeductionTypeEnum.DEDUCTION_INTERNAL.equals(deductionTypeEnum)) {
            actualAmount = amount;
        } else if (DeductionTypeEnum.DEDUCTION_EXTERNAL.equals(deductionTypeEnum)) {
            actualAmount = amount.add(merchantFee);
        } else {
            throw new PaymentException(ExceptionCode.UNSUPPORTED_DEDUCTION_TYPE);
        }
        log.info("calculateActualAmount actualAmount={}", actualAmount);
        return actualAmount;
    }

    /**
     * 计算到账金额
     */
    public BigDecimal calculateAccountAmount(DeductionTypeEnum deductionTypeEnum,
                                             BigDecimal amount,
                                             BigDecimal merchantFee) {
        BigDecimal accountAmount;
        if (DeductionTypeEnum.DEDUCTION_INTERNAL.equals(deductionTypeEnum)) {
            accountAmount = amount.subtract(merchantFee);
        } else if (DeductionTypeEnum.DEDUCTION_EXTERNAL.equals(deductionTypeEnum)) {
            accountAmount = amount;
        } else {
            throw new PaymentException(ExceptionCode.UNSUPPORTED_DEDUCTION_TYPE);
        }
        log.info("calculateAccountAmount accountAmount={}", accountAmount);
        return accountAmount;
    }

    /**
     * 计算通道成本
     */
    public BigDecimal calculateChannelCost(BigDecimal amount,
                                           BigDecimal singleRate,
                                           BigDecimal singleFee,
                                           BigDecimal minCost,
                                           BigDecimal channelCost) {
        if (Objects.nonNull(channelCost)) {
            return channelCost;
        }
        channelCost = amount.multiply(singleRate).add(singleFee);
        if (Objects.nonNull(minCost)) {
            channelCost = channelCost.compareTo(minCost) >= 0 ? channelCost : minCost;
        }
        log.info("calculateChannelCost channelCost={}", channelCost);
        return channelCost;
    }

    /**
     * 计算平台利润
     */
    public BigDecimal calculatePlatformProfit(BigDecimal merchantFee,
                                              BigDecimal merchantProfit,
                                              BigDecimal channelCost) {
        BigDecimal platformProfit = merchantFee.subtract(merchantProfit).subtract(channelCost);
        log.info("calculatePlatformProfit platformProfit={}", platformProfit);
        return platformProfit;
    }

    /**
     * 校验收款平台利润
     */
    /*public void verifyPayPlatformProfit(TradePayOrder payOrder) {
        if (Objects.isNull(payOrder)) {
            throw new PaymentException(ExceptionCode.GENERAL_ERROR, "verify pay transaction profit");
        }
        String tradeNo = payOrder.getTradeNo();
        BigDecimal platformProfit = payOrder.getPlatformProfit();
        if (Objects.nonNull(platformProfit) && (platformProfit.compareTo(BigDecimal.ZERO) < 0)) {
            log.info("payOrder verifyPlatformProfit tradeNo={}, platformProfit={}", tradeNo, platformProfit);
            String merchantId = payOrder.getMerchantId() + "/" + payOrder.getMerchantName();
            String channelCode = payOrder.getChannelCode() + "/" + payOrder.getPaymentMethod();
            String platformProfitStr = platformProfit.setScale(2, RoundingMode.HALF_DOWN) + " " + payOrder.getCurrency();
            rocketMqProducer.tgSendProfitNotifyTgMessage(merchantId, tradeNo, channelCode, platformProfitStr);
            //阻断交易
            //throw new PaymentException("payin " + payOrder.getTradeNo() + " rate configuration is incorrect. profit less zero");
        }
    }*/

    /**
     * 校验代付平台利润
     */
    /*public void verifyCashPlatformProfit(TradeCashOrder cashOrder) {
        if (Objects.isNull(cashOrder)) {
            throw new PaymentException(ExceptionCode.GENERAL_ERROR, "verify cash transaction profit");
        }
        String tradeNo = cashOrder.getTradeNo();
        BigDecimal platformProfit = cashOrder.getPlatformProfit();
        if (Objects.nonNull(platformProfit) && (platformProfit.compareTo(BigDecimal.ZERO) < 0)) {
            log.info("cashOrder verifyPlatformProfit tradeNo={}, platformProfit={}", tradeNo, platformProfit);
            String merchantId = cashOrder.getMerchantId() + "/" + cashOrder.getMerchantName();
            String channelCode = cashOrder.getChannelCode() + "/" + cashOrder.getPaymentMethod();
            String platformProfitStr = platformProfit.setScale(2, RoundingMode.HALF_DOWN) + " " + cashOrder.getCurrency();
            rocketMqProducer.tgSendProfitNotifyTgMessage(merchantId, tradeNo, channelCode, platformProfitStr);
        }
    }*/

    /**
     * 校验提现平台利润
     */
    /*public void verifyWithdrawPlatformProfit(TradeWithdrawOrder withdrawOrder) {
        if (Objects.isNull(withdrawOrder)) {
            throw new PaymentException(ExceptionCode.GENERAL_ERROR, "verify withdraw transaction profit");
        }
        String tradeNo = withdrawOrder.getTradeNo();
        BigDecimal platformProfit = withdrawOrder.getPlatformProfit();
        if (Objects.nonNull(platformProfit) && (platformProfit.compareTo(BigDecimal.ZERO) < 0)) {
            log.info("withdrawOrder verifyPlatformProfit tradeNo={}, platformProfit={}", tradeNo, platformProfit);
            String merchantId = withdrawOrder.getMerchantId() + "/" + withdrawOrder.getMerchantName();
            String channelCode = withdrawOrder.getChannelCode() + "/" + withdrawOrder.getPaymentMethod();
            String platformProfitStr = platformProfit.setScale(2, RoundingMode.HALF_DOWN) + " " + withdrawOrder.getCurrency();
            rocketMqProducer.tgSendProfitNotifyTgMessage(merchantId, tradeNo, channelCode, platformProfitStr);
        }
    }*/

}
