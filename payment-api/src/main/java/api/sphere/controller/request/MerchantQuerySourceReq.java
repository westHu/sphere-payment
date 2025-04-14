package api.sphere.controller.request;

import api.sphere.config.EnumValid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import share.sphere.enums.QuerySourceEnum;

@Data
public class MerchantQuerySourceReq {

    /**
     * 商户ID
     */
    @NotNull(message = "merchantId is required")
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 来源
     */
    @EnumValid(target = QuerySourceEnum.class, transferMethod = "getCode", message = "querySource not support")
    private Integer querySource = 1;

}
