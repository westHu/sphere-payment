package app.sphere.query.dto;


import lombok.Data;

@Data
public class TradeOrderStatusInquiryDTO {

    /**
     * 订单类型
     */
    private Integer tradeType;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 状态
     */
    private String status;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 金额
     */
//    private MoneyDTO money;

    /**
     * 备注
     */
    private String remark;

}
