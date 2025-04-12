package app.sphere.query.param;


import lombok.Data;

@Data
public class PaymentChannelListParam {

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

}
