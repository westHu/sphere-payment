package infrastructure.sphere.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户基础实体类
 * 所有数据库实体类的父类，提供基础字段
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseTradeEntity extends BaseEntity {

    // ============== 订单信息 ==============
    /**
     * 收款单号
     * 关联的收款订单号
     */
    private String tradeNo;

    /**
     * 商户传入的订单号
     */
    private String orderNo;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 交易目的
     * 订单的交易用途说明
     */
    private String purpose;
}
