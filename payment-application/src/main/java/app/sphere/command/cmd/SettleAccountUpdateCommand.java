package app.sphere.command.cmd;


import lombok.Data;
import share.sphere.enums.AccountOptTypeEnum;

import java.math.BigDecimal;

@Data
public class SettleAccountUpdateCommand {

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
     * 操作类型
     */
    private AccountOptTypeEnum accountOptType;

    /**
     * 币种
     */
    private String currency;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 商户分润
     */
    private BigDecimal merchantProfit;

    /**
     * 渠道成本费用
     */
    private BigDecimal channelCost;

    /**
     * 商户到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 平台盈利
     */
    private BigDecimal platformProfit;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 扣费方式
     */
    private Integer deductionType;

    /**
     * 结算时间
     */
    private String settleTime;

    /**
     * 地区
     */
    private String region;
}
