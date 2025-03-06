package com.paysphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class TradeTransferOrderDTO {

    /**
     * 订单详情
     */
    private TradeTransferOrderDetailDTO transferOrderDetail;

    /**
     * 订单时间轴
     */
    private List<TradeOrderTimeLineDTO> timeLine;

}
