package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantPageParam extends PageParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户性质：个人、企业
     */
    private Integer merchantType;

    /**
     * 商户状态
     */
    private Integer status;

    /**
     * 创建开始时间
     */
    private String createStartTime;

    /**
     * 创建开始时间
     */
    private String createEndTime;

    /**
     * 地区
     */
    private Integer area;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道
     */
    private String channelCode;

}
