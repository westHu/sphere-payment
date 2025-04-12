package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradeMerchantStatisticsSnapshotReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 开始时间
     */
    @NotBlank(message = "startTime is required")
    private String startDate;

    /**
     * 结束时间
     */
    @NotBlank(message = "endTime is required")
    private String endDate;

    /**
     * 是否包含当天
     */
    private Boolean includeToday = Boolean.FALSE;

}
