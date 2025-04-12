package api.sphere.controller.request;

import lombok.Data;

import java.util.List;

@Data
public class SettleReSettlementReq {


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
