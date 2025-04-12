package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeTransferCommand {

    /**
     * 转账目的
     */
    private String purpose;

    /**
     * 转出商户ID
     */
    private String fromMerchantId;

    /**
     * 转出商户名称
     */
    private String fromMerchantName;

    /**
     * 转出账户
     */
    private String fromAccountNo;

    /**
     * 转入商户ID
     */
    private String toMerchantId;

    /**
     * 转入商户名称
     */
    private String toMerchantName;

    /**
     * 转入账户
     */
    private String toAccountNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 转账金额
     */
    private BigDecimal amount;

    /**
     * 申请人
     */
    private String applyOperator;

}
