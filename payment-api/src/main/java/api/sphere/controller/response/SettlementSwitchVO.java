package api.sphere.controller.response;

import lombok.Data;

@Data
public class SettlementSwitchVO {

    /**
     * 结算类型
     */
    private String settleType;

    /**
     * 开关
     */
    private boolean onOff;

}
