package api.sphere.controller.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleAccountBalanceDetailVO {


    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户类型
     */
    private String accountType;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 可用余额
     */
    private BigDecimal availableBalance;

    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance;

    /**
     * 待结算余额
     */
    private BigDecimal toSettleBalance;

    /**
     * 币种
     */
    private String currency;

}
