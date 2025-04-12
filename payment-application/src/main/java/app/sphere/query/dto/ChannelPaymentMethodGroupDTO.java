package app.sphere.query.dto;

import infrastructure.sphere.db.entity.PaymentChannelMethod;
import lombok.Data;

import java.util.List;

@Data
public class ChannelPaymentMethodGroupDTO {

    /**
     * 渠道支付方式列表
     */
    List<PaymentChannelMethod> paymentChannelMethodList;
    /**
     * 渠道编码
     */
    private String channelCode;
    /**
     * 渠道名称
     */
    private String channelName;
    /**
     * 0/1 status
     * 状态
     */
    private boolean status;
    /**
     * remark
     */
    private Integer count;
}
