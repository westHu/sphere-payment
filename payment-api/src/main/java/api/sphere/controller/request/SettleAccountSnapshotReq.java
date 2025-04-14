package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettleAccountSnapshotReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 开始时间
     */
    @NotBlank(message = "startDate is required")
    private String startDate;

    /**
     * 结束时间
     */
    @NotBlank(message = "endDate is required")
    private String endDate;
}
