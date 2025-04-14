package api.sphere.controller.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentChannelMethodPageReq extends PageReq {

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

    /**
     * 状态
     */
    private Boolean status;

}
