package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantChannelConfigUpdateByChannelReq {

    /**
     * 渠道编码
     */
    @NotBlank(message = "channelCode is required")
    private String channelCode;

    /**
     * 商户渠道配置状态
     */
    @NotNull(message = "status is required")
    private Boolean status;

}
