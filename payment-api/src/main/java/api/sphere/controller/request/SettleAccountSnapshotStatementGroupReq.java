package api.sphere.controller.request;

import lombok.Data;

@Data
public class SettleAccountSnapshotStatementGroupReq extends PageReq {

    /**
     * 日期
     */
    private String accountStartDate;

    /**
     * 日期
     */
    private String accountEndDate;
}
