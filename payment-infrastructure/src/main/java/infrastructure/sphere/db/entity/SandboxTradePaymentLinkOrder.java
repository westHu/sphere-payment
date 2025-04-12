package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 沙箱支付链接订单实体
 * 用于测试环境的支付链接订单，包含支付链接的生成和管理
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sandbox_trade_payment_link_order")
public class SandboxTradePaymentLinkOrder extends BaseEntity {

    // ============== 订单信息 ==============
    /**
     * 支付链接单号
     * 系统生成的唯一订单号
     */
    private String linkNo;

    // ============== 商户信息 ==============
    /**
     * 商户ID
     * 商户的唯一标识符
     */
    private String merchantId;

    /**
     * 商户名称
     * 商户的显示名称
     */
    private String merchantName;

    // ============== 支付信息 ==============
    /**
     * 支付方式
     * 支持的支付方式，如：ALIPAY、WECHAT等
     */
    private String paymentMethod;

    /**
     * 币种
     * 交易使用的货币类型，如：CNY、USD等
     */
    private String currency;

    /**
     * 金额
     * 订单的交易金额
     */
    private BigDecimal amount;

    // ============== 链接信息 ==============
    /**
     * 支付链接
     * 生成的支付链接URL
     */
    private String paymentLink;

    // ============== 状态控制 ==============
    /**
     * 状态
     * 0: 未支付
     * 1: 已支付
     * 2: 已过期
     * 3: 已关闭
     */
    private Integer linkStatus;

    // ============== 其他信息 ==============
    /**
     * 备注
     * 订单的备注信息
     */
    private String notes;

    /**
     * 地区
     * 订单所属地区，如：CN、US等
     */
    private String region;
    
    /**
     * 版本
     * 订单的版本号，用于乐观锁
     */
    private Integer version;

    // ============== 扩展属性 ==============
    /**
     * 扩展信息
     * 用于存储订单的额外配置信息，JSON格式
     */
    private String attribute;
}
