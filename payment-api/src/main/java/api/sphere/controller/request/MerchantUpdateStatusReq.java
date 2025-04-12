package api.sphere.controller.request;


import api.sphere.config.EnumValid;
import share.sphere.enums.MerchantStatusEnum;
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
