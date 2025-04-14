package app.sphere.command.cmd;

import lombok.Data;

@Data
public class TradeFileJobCommand {

    /**
     * 交易日期
     */
    private String tradeDate;

    /**
     * 渠道名称
     */
    private String channelName;

}
