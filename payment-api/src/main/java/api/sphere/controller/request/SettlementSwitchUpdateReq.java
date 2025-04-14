package api.sphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SettlementSwitchUpdateReq {

    /**
     * 结算类型
     */
    private String settleType;

    /**
     * 结算开关
     */
    @NotNull(message = "settlementSwitch is required")
    private Boolean settlementSwitch;

}
