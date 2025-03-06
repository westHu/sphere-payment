package com.paysphere.controller.request;

import com.paysphere.config.EnumValid;
import com.paysphere.enums.MerchantQuerySourceEnum;
import lombok.Data;

@Data
public class MerchantQuerySourceReq {

    /**
     * 来源
     */
    @EnumValid(target = MerchantQuerySourceEnum.class, transferMethod = "getCode", message = "querySource not support")
    private Integer querySource;

}
