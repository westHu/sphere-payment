package app.sphere.command.cmd;

import lombok.Data;

@Data
public class TradePayOrderTimeOutJobCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

}
