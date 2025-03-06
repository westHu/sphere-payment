
package com.paysphere.mq.dto.settle;

import lombok.Data;

@Data
public class UnfrozenMessageDTO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 外部订单号
     */
    private String outerNo;

}
