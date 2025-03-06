package com.paysphere.query.param;

import lombok.Data;

@Data
public class SettleAccountSnapshotStatementGroupParam extends PageParam {

    /**
     * 日期
     */
    private String accountStartDate;

    /**
     * 日期
     */
    private String accountEndDate;

}
