package com.paysphere.job.param;

import lombok.Data;

@Data
public class AccountFlowOrderRevisionJobParam {

    /**
     * 起始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户号
     */
    private String accountNo;
}
