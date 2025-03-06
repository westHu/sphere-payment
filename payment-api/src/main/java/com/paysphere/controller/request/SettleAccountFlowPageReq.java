package com.paysphere.controller.request;

import lombok.Data;

@Data
public class SettleAccountFlowPageReq extends PageReq {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户账号
     */
    private String accountNo;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 账户资金方向 1 收入 -1 支出
     */
    private Integer accountDirection;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}
