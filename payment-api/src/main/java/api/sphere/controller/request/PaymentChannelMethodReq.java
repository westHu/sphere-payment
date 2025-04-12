package api.sphere.controller.request;

import lombok.Data;

/**
 * 查询支付方式
 */
@Data
public class PaymentChannelMethodReq {

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 支付方向
     */
    private Integer paymentDirection;

}
