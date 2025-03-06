package com.paysphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class TradePayOrderDTO {

    /**
     * 订单详情
     */
    private TradePayOrderDetailDTO payOrderDetail;

    /**
     * 订单时间轴
     */
    private List<TradeOrderTimeLineDTO> timeLine;


}
