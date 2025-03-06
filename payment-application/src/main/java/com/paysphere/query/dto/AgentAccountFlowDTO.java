package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AgentAccountFlowDTO {

    /**
     * '交易订单'
     */
    private String tradeNo;

    /**
     * '账户资金方向 1 收入 -1 支出
     */
    private Integer accountDirection;

    /**
     * '账户资金方向描述'
     */
    private String accountDirectionDesc;

    /**
     * '币种类型'
     */
    private String currency;

    /**
     * '变动金额'
     */
    private BigDecimal amount;

    /**
     * 流水时间
     */
    private LocalDateTime flowTime;


}
