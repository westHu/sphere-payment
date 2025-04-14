package app.sphere.query.dto;

import infrastructure.sphere.db.entity.PaymentChannelMethod;
import lombok.Data;

import java.util.List;

@Data
public class PaymentChannelMethodGroupDTO {

    /**
     * 渠道支付方式列表
     */
    List<PaymentChannelMethod> paymentChannelMethodList;
    /**
     * payment
     * 支付方式编码
     */
    private String paymentMethod;
    /**
     * detailed name
     * 名称
     */
    private String paymentName;
    /**
     * 交易方向 见枚举
     */
    private Integer paymentDirection;
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
