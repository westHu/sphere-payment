package app.sphere.command.cmd;

import lombok.Data;

@Data
public class TradeRechargeCommand {

    /**
     * 充值单号
     */
    private String tradeNo;

    /**
     * 充值状态
     */
    private boolean status;

    /**
     * 证明 譬如图片截图
     */
    private String proof;

}
