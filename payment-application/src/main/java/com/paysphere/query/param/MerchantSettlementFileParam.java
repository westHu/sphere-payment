package com.paysphere.query.param;

import lombok.Data;

@Data
public class MerchantSettlementFileParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 开始结算时间
     */
    private String startSettleDate;

    /**
     * 结束结算时间
     */
    private String endSettleDate;

}
