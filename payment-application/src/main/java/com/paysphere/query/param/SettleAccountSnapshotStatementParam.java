package com.paysphere.query.param;

import lombok.Data;

@Data
public class SettleAccountSnapshotStatementParam extends PageParam {

    /**
     * 日期
     */
    private String accountDate;

    /**
     * 账户类型
     */
    private Integer accountType;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 状态
     */
    private Boolean status;
}
