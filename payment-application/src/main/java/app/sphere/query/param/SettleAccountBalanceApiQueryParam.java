package app.sphere.query.param;

import lombok.Data;

@Data
public class SettleAccountBalanceApiQueryParam {

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 扩展
     */
    private String additionalInfo;
}
