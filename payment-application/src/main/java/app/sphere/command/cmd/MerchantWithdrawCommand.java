package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantWithdrawCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 提现类型
     */
    private String withdrawTo;

    /**
     * from账户
     */
    private String fromAccountNo;

    /**
     * to账户
     */
    private String toAccountNo;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 目的
     */
    private String purpose;

}
