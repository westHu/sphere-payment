package app.sphere.command.dto;

import lombok.Data;

@Data
public class SettleFileDTO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 时间
     */
    private String tradeTime;


    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 收款币种
     */
    private String currency = "";

    /**
     * 收款金额  收款金额 - 商户手续费 = 到账金额
     */
    private String amount = "0";

    /**
     * 商户手续费  商户手续费 = 商户分润 + 渠道成本 + 平台利润
     */
    private String merchantFee = "0";

    /**
     * 到账金额
     */
    private String accountAmount = "0";

    /**
     * 商户(代理商)分润
     */
    private String merchantProfit = "0";

    /**
     * 通道成本金额
     */
    private String channelCost = "0";

    /**
     * 平台利润
     */
    private String platformProfit = "0";

}
