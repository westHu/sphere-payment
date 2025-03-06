package com.paysphere.query.dto;

import lombok.Data;

@Data
public class TradeMerchantDailySnapchatDTO extends TradeChannelDailySnapchatDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;
}
