package app.sphere.query.param;

import lombok.Data;

@Data
public class SettleAmountParam {

    /**
     * 账户(收款)
     */
    private String accountNo;

    /**
     * 结算日期
     */
    private String settleStartDate;

    /**
     * 结算日期
     */
    private String settleEndDate;

}
