package com.paysphere.controller.request;


import com.paysphere.config.EnumValid;
import com.paysphere.enums.MerchantStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantUpdateStatusReq extends MerchantIdReq {

    /**
     * 商户状态
     */
    @NotNull(message = "status is required")
    @EnumValid(target = MerchantStatusEnum.class, transferMethod = "getCode", message = "status type not support")
    private Integer status;

}
