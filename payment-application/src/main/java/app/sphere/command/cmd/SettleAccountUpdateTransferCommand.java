package app.sphere.command.cmd;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleAccountUpdateTransferCommand {

    /**
     * 交易单号
     */
    private String tradeNo;



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
     * 商户到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 渠道成本费用
     */
    private BigDecimal channelCost;


    /**
     * 平台盈利
     */
    private BigDecimal platformProfit;

}
