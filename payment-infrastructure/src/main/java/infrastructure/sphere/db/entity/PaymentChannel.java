package infrastructure.sphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付渠道实体
 * 记录系统中对接的支付渠道信息，包括渠道基础信息、接口配置、授权信息等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "payment_channel")
public class PaymentChannel extends BaseEntity {

    // ============== 基础信息 ==============
    /**
     * 渠道编码
     * 渠道的唯一标识符，如：ALIPAY、WECHAT等
     */
    private String channelCode;

    /**
     * 渠道名称
     * 渠道的显示名称，如：支付宝、微信支付等
     */
    private String channelName;

    /**
     * 渠道类型
     * 1: 银行
     * 2: 三方支付
     * 3: 四方支付
     */
    private String channelType;

    // ============== 接口配置 ==============
    /**
     * 返回模式
     * 0: 收银台模式
     * 1: 收款号模式
     * 4: 混合模式
     */
    private Integer returnMode;

    /**
     * API接口地址
     * 渠道的API接口地址，用于支付请求
     */
    private String url;

    // ============== 授权信息 ==============
    /**
     * 渠道授权信息
     * 渠道的授权信息，如：API密钥、证书等
     */
    private String license;

    // ============== 状态控制 ==============
    /**
     * 渠道状态
     * true: 启用
     * false: 禁用
     */
    private boolean status;

    // ============== 扩展属性 ==============
    /**
     * 扩展属性
     * 用于存储渠道的额外配置信息，JSON格式
     */
    private String attribute;
}