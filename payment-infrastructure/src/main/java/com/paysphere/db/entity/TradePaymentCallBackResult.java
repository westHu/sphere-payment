package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收款订单回调结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "trade_pay_callback_result")
public class TradePaymentCallBackResult extends BaseEntity {

    /**
     * 收款单号
     */
    private String tradeNo;

    /**
     * 回调时间
     */
    private Integer callBackTime;

    /**
     * 回调状态
     */
    private Integer callBackStatus;

    /**
     * 回调结果
     */
    private String callBackResult;

    /**
     * 扩展字段
     */
    private String attribute;

}
