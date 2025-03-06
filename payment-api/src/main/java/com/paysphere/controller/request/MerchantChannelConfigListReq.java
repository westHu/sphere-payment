package com.paysphere.controller.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantChannelConfigListReq extends MerchantIdReq {

    /**
     * 状态
     */
    private Boolean status;

}
