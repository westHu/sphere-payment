package app.sphere.query.dto;

import lombok.Data;

@Data
public class TradeChannelDailySnapchatDTO extends TradePlatformDailySnapchatDTO {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

}
