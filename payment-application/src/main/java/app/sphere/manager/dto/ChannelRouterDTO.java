package app.sphere.manager.dto;

import infrastructure.sphere.db.entity.*;
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
