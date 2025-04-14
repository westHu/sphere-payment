package app.sphere.command.cmd;

import lombok.Data;

@Data
public class TradeCallbackCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 操作人
     */
    private String operator;

}
