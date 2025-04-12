package app.sphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class TradeCashOrderDTO {

    /**
     * 订单详情
     */
    private TradeCashOrderDetailDTO cashOrderDetail;

    /**
     * 订单时间轴
     */
    private List<TradeOrderTimeLineDTO> timeLine;


}
