package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentChannelPageParam extends PageParam {

    /**
     * 渠道编号
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 是否需要进件
     */
    private Boolean division;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否关联 (渠道 与 支付方式)
     */
    private Boolean related;
}
