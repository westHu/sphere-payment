package api.sphere.controller.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户账户余额
 */
@Data
public class MerchantAccountVO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户角色
     */
    private Integer role;

    /**
     * 账户类型
     */
    private Integer accountType;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 当前余额
     */
    private BigDecimal currentBalance;

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
