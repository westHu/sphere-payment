package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户沙箱基本配置实体
 * 用于测试环境的商户配置信息，包含商户基础信息、回调配置、安全配置等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sandbox_merchant_config")
public class MerchantSandboxConfig extends BaseMerchantEntity {

    // ============== 回调配置 ==============
    /**
     * 商户收款完成回调地址
     * 支付完成后系统回调的URL地址
     */
    private String finishPaymentUrl;

    /**
     * 商户出款完成回调地址
     * 代付完成后系统回调的URL地址
     */
    private String finishPayoutUrl;

    /**
     * 商户退款完成回调地址
     * 退款完成后系统回调的URL地址
     */
    private String finishRefundUrl;

    /**
     * 商户支付完成跳转地址
     * 支付完成后跳转的URL地址
     */
    private String finishRedirectUrl;

    // ============== 安全配置 ==============
    /**
     * 商户秘钥
     * 用于签名验证的商户密钥
     */
    private String merchantSecret;

    /**
     * 商户公钥
     * 用于验签的商户公钥
     */
    private String publicKey;

    /**
     * 商户ip白名单
     * 允许访问的IP地址列表，多个IP用逗号分隔
     */
    private String ipWhiteList;

    // ============== 扩展属性 ==============
    /**
     * 扩展信息
     * 用于存储商户的额外配置信息，JSON格式
     */
    private String attribute;

}
