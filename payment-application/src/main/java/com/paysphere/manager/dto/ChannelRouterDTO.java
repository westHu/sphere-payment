package com.paysphere.manager.dto;

import com.paysphere.db.entity.PaymentChannel;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.db.entity.PaymentMethod;
import lombok.Data;

@Data
public class ChannelRouterDTO {

    /**
     * 渠道
     */
    private PaymentChannel paymentChannel;

    /**
     * 支付方式
     */
    private PaymentMethod paymentMethod;

    /**
     * 渠道 + 支付方式
     */
    private PaymentChannelMethod paymentChannelMethod;
}
