package com.paysphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class TradeWithdrawOrderDTO {

    /**
     * 订单详情
     */
    private TradeWithdrawOrderDetailDTO withdrawOrderDetail;

    /**
     * 订单时间轴
     */
    private List<TradeOrderTimeLineDTO> timeLine;

}
