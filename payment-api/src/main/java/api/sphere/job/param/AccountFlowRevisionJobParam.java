package api.sphere.job.param;

import lombok.Data;

@Data
public class AccountFlowRevisionJobParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户号
     */
    private String accountNo;

}
