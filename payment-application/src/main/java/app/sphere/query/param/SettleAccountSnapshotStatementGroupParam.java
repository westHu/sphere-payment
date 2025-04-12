package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
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
