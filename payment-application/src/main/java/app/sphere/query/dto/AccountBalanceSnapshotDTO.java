package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountBalanceSnapshotDTO {


    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 日期
     */
    private String accountDate;

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
