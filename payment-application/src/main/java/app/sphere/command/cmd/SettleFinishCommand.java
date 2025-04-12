package app.sphere.command.cmd;

import lombok.Data;

@Data
public class SettleFinishCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 结算状态
     */
    private boolean settleStatus;

    /**
     * 结算时间
     */
    private String settleTime;

}
