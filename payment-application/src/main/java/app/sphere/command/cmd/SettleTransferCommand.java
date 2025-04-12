package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleTransferCommand {

    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 审核状态
     */
    private Boolean reviewStatus;

    /**
     * 转入商户ID
     */
    private String transferToMerchantId;

    /**
     * 转入商户名称
     */
    private String transferToMerchantName;

    /**
     * 转入账户
     */
    private String transferToAccount;

    /**
     * 转账金额
     */
    private BigDecimal amount;

    /**
     * 商户(代理商)分润
     */
    private BigDecimal merchantProfit;

    /**
     * 商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 通道成本金额
     */
    private BigDecimal channelCost;

    /**
     * 平台利润
     */
    private BigDecimal platformProfit;
}
