package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantSettlementFileReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
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
