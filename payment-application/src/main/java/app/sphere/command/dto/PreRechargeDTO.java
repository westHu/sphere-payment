package app.sphere.command.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PreRechargeDTO {

    /**
     * 充值单号
     */
    private String tradeNo;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 充值账户
     */
    private String bankAccount;

    /**
     * 持卡人姓名
     */
    private String bankAccountName;

    /**
     * 充值币种 - 用户充值的原始币种
     */
    private String rechargeCurrency;

    /**
     * 充值金额 - 用户充值的原始金额（以充值币种计算）
     */
    private BigDecimal rechargeAmount;

    /**
     * 充值金额 - 随机金额
     */
    private BigDecimal randomAmount;

    /**
     * 兑换率 - 充值币种到入账币种的兑换率
     */
    private BigDecimal exchangeRate;

    /**
     * 入账币种 - 资金入账的币种，可能与充值币种不同
     */
    private String currency;

    /**
     * 入账金额 - 实际入账的金额（以入账币种计算）
     */
    private BigDecimal amount;

}
