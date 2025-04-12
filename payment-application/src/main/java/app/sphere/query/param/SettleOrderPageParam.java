package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SettleOrderPageParam extends PageParam {

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
     * 渠道编码
     */
    private String channelCode;

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
