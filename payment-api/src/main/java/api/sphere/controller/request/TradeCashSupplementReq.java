package api.sphere.controller.request;

import lombok.Data;

@Data
public class TradeCashSupplementReq extends TradeNoReq {

    /**
     * 操作员
     */
    private String operator;

}
