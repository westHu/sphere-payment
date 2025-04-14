package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户基本配置
 * 记录商户的基础配置信息，包括安全配置、回调地址、业务配置等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant_config")
public class MerchantConfig extends BaseMerchantEntity {

    // ============== 安全配置 ==============
    /**
     * 商户秘钥
     * 用于API接口签名验证
     */
    private String merchantSecret;

    /**
     * 商户公钥
     * 用于数据加密传输
     */
    private String publicKey;

    /**
     * 商户ip白名单
     * 允许访问的IP地址列表，多个IP用逗号分隔
     */
    private String ipWhiteList;

    // ============== 回调地址 ==============
    /**
     * 商户收款完成回调地址
     * 支付成功后系统回调通知地址
     */
    private String finishPaymentUrl;

    /**
     * 商户出款完成回调地址
     * 出款成功后系统回调通知地址
     */
    private String finishPayoutUrl;

    /**
     * 商户退款完成回调地址
     * 退款成功后系统回调通知地址
     */
    private String finishRefundUrl;

    /**
     * 商户支付完成跳转地址
     * 支付完成后跳转的页面地址
     */
    private String finishRedirectUrl;

    // ============== 业务配置 ==============
    /**
     * 支付链接设置
     * JSON格式，包含支付链接相关配置
     */
    private String paymentLinkSetting;

    /**
     * 凭证设置
     * JSON格式，包含收据/发票相关配置
     */
    private String receiptSetting;

    // ============== 扩展属性 ==============
    /**
     * 扩展信息
     * JSON格式，用于存储其他扩展配置
     */
    private String attribute;
}
