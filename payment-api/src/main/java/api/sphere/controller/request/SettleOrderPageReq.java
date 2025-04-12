package api.sphere.controller.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SettleOrderPageReq extends PageReq {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 结算状态
     */
    private Integer settleStatus;

    /**
     * 交易开始时间
     */
    private String tradeStartTime;

    /**
     * 交易开始时间
     */
    private String tradeEndTime;

}
