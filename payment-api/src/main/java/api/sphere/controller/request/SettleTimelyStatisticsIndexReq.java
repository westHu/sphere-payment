package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettleTimelyStatisticsIndexReq {

    /**
     * 开始时间
     */
    @NotBlank(message = "startTime is required")
    private String startTime;

    /**
     * 结束时间
     */
    @NotBlank(message = "endTime is required")
    private String endTime;

}
