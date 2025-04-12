package app.sphere.manager.dto;

import infrastructure.sphere.db.entity.PaymentChannel;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
import infrastructure.sphere.db.entity.PaymentMethod;
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
