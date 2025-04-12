package app.sphere.command.cmd;


import share.sphere.enums.AccountOptTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SettleAccountUpdateCashCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

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
     * 实际扣款
     */
    private BigDecimal actualAmount;

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
     * 扣费方式
     */
    private Integer deductionType;

    /**
     * 地区
     */
    private String region;

}
