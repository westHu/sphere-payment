package com.paysphere.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.paysphere.TradeConstant;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TradeOrderTimeLineDTO {

    /**
     * 时间轴时间
     */
    @JsonFormat(pattern = TradeConstant.FORMATTER_0)
    private LocalDateTime lineTime;

    /**
     * 时间轴消息
     */
    private String lineMessage;

    /**
     * 时间轴操作员
     */
    private String lineOperator;

    /**
     * 状态
     */
    private boolean status;
}
