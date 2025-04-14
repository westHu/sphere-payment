package infrastructure.sphere.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户基础实体类
 * 所有数据库实体类的父类，提供基础字段
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BasePaymentEntity extends BaseEntity {

    // ============== 渠道信息 ==============
    /**
     * 渠道编码
     * 支付渠道的唯一标识符，如：ALIPAY、WECHAT等
     */
    private String channelCode;

    /**
     * 渠道名称
     * 支付渠道的显示名称，如：支付宝、微信支付等
     */
    private String channelName;
}
