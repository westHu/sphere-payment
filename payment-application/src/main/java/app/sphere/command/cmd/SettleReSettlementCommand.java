package app.sphere.command.cmd;

import lombok.Data;

import java.util.List;

@Data
public class SettleReSettlementCommand {


    /**
     * ID列表
     */
    private List<Long> idList;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 操作员
     */
    private String operator;
}
