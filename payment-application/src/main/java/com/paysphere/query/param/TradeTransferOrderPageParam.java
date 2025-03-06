package com.paysphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TradeTransferOrderPageParam extends PageParam {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 转账商户ID
     */
    private String merchantId;

    /**
     * 转账账户
     */
    private String accountNo;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 交易开始时间
     */
    private String tradeStartTime;

    /**
     * 交易结束时间
     */
    private String tradeEndTime;

}
