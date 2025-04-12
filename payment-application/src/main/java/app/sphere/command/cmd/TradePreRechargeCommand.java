package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradePreRechargeCommand {

    /**
     * 充值目的
     */
    private String purpose;

    /**
     * 充值商户ID
     */
    private String merchantId;

    /**
     * 充值商户名称
     */
    private String merchantName;

    /**
     * 充值账户
     */
    private String accountNo;

    /**
     * 充值币种 - 用户充值的原始币种
     */
    private String rechargeCurrency;

    /**
     * 充值金额 - 用户充值的原始金额（以充值币种计算）
     */
    private BigDecimal rechargeAmount;

    /**
     * 支付方式
     */
    private String paymentMethod;

}
