package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantChannelConfigDivisionReq {
    /**
     * 商户编号
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 商户名称
     */
    @NotBlank(message = "merchantName is required")
    private String merchantName;

    /**
     * 渠道编码
     */
    @NotBlank(message = "channelCode is required")
    private String channelCode;

    /**
     * 渠道名称
     */
    @NotBlank(message = "channelName is required")
    private String channelName;

    /**
     * 渠道分开账户（子账户）
     */
    @NotBlank(message = "channelDivision is required")
    private String channelDivision;


}
