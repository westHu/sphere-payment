package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountFlowCsvDTO {

    /**
     * 关联交易单号
     */
    private String tradeNo;

    /**
     * 商户订单号
     */
    private String merchantNo;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户资金方向 1 收入 -1 支出
     */
    private String accountDirection;

    /**
     * 币种
     */
    private String currency;

    /**
     * 变动金额
     */
    private BigDecimal amount;

    /**
     * 流水记录时间
     */
    private String flowTime;
}
