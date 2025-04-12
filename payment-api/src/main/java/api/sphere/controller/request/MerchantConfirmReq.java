package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantConfirmReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 主题
     */
    @NotBlank(message = "subject is required")
    private String subject;

    /**
     * 验证key
     */
    @NotBlank(message = "token is required")
    private String token;

    /**
     * 时间
     */
    @NotBlank(message = "time is required")
    private String time;
}
