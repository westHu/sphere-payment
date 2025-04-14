package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountSnapshotSettlementDTO {

    /**
     * 日期
     */
    private String accountDate;

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
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 期初余额
     */
    private BigDecimal openingBalance;

    /**
     * 期末余额
     */
    private BigDecimal endingBalance;

    /**
     * 变动金额
     */
    private BigDecimal changeAmount;

    /**
     * 流水汇总金额
     */
    private BigDecimal flowSummaryAmount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 状态  1正常  -1异常
     */
    private boolean status;
}
