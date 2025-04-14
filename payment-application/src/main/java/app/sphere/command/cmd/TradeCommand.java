package app.sphere.command.cmd;

import lombok.Data;

@Data
public class TradeCommand {

    /**
     * 地区
     */
    private String region;

    /**
     * 外部单号 商户平台订单号
     */
    private String orderNo;

    /**
     * 目的
     */
    private String purpose;

    /**
     * 商品详情
     */
    private String productDetail;

    /**
     * 金额
     */
    private MoneyCommand money;

    /**
     * 商户信息
     */
    private MerchantCommand merchant;

    /**
     * 回调地址
     */
    private String callbackUrl;

    /**
     * 跳转地址
     */
    private String redirectUrl;

    /**
     * 附件信息 FIXME 没看到使用地方
     */
    private String additionalInfo;
}
