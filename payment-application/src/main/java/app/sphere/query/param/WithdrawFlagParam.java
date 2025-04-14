package app.sphere.query.param;

import lombok.Data;

@Data
public class WithdrawFlagParam {

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 提现日期
     */
    private String withdrawDate;

}
