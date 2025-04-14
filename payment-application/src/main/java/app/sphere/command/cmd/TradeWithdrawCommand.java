package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeWithdrawCommand {

    /**
     * 目的
     */
    private String purpose;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户账户号
     */
    private String accountNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 支付方式
     */
    private String paymentMethod;


    private String bankAccount;


    private String bankAccountName;

    /**
     * 申请人
     */
    private String applyOperator;

}
