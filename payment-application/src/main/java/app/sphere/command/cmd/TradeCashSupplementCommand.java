package app.sphere.command.cmd;

import lombok.Data;

@Data
public class TradeCashSupplementCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 操作员
     */
    private String operator;

}
