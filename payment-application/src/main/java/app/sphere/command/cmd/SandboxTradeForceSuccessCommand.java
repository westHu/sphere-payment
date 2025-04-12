package app.sphere.command.cmd;

import lombok.Data;

@Data
public class SandboxTradeForceSuccessCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 是否成功
     */
    private boolean success;

}
