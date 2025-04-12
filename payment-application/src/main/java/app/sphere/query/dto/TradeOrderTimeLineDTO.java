package app.sphere.query.dto;

import lombok.Data;

@Data
public class TradeOrderTimeLineDTO {

    /**
     * 时间轴时间
     */
    private String lineTime;

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
