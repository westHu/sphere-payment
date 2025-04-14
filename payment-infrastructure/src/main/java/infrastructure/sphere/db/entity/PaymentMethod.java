package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付方式实体
 * 定义系统中支持的支付方式，包括基础信息、支付配置、展示信息等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "payment_method")
public class PaymentMethod extends BaseEntity {

    // ============== 基础信息 ==============
    /**
     * 支付方式编码
     * 支付方式的唯一标识符，如：ALIPAY_APP、WECHAT_APP等
     */
    private String paymentMethod;

    // ============== 支付配置 ==============
    /**
     * 支付方向
     * 1: 收款
     * 2: 付款
     * 3: 双向
     */
    private Integer paymentDirection;

    /**
     * 支付方式类型
     * 1: 信用卡
     * 2: 虚拟号
     * 3: 二维码
     * 4: 快捷支付
     * 5: 网银支付
     */
    private Integer paymentType;

    // ============== 展示信息 ==============
    /**
     * 支付方式图标
     * 支付方式的图标URL
     */
    private String paymentIcon;

    // ============== 区域配置 ==============
    /**
     * 支持的地区
     * 支付方式支持的地区，如：CN、US等
     */
    private String region;

    // ============== 状态控制 ==============
    /**
     * 支付方式状态
     * true: 启用
     * false: 禁用
     */
    private boolean status;

    // ============== 扩展属性 ==============
    /**
     * 扩展属性
     * 用于存储支付方式的额外配置信息，JSON格式
     */
    private String attribute;
}
