package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentLinkSettingReq extends MerchantIdReq {

    /**
     * 配置参数
     */
    @NotBlank(message = "paymentLinkSetting is required")
    private String paymentLinkSetting;

}
