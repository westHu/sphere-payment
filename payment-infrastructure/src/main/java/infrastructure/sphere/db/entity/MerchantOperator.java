package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商户操作员
 * 记录商户的操作员信息，包括基础信息、认证信息、安全信息等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant_operator")
public class MerchantOperator extends BaseEntity {

    // ============== 基础信息 ==============
    /**
     * 商户ID
     * 操作员所属商户的唯一标识符
     */
    private String merchantId;

    /**
     * 商户名称
     * 操作员所属商户的名称
     */
    private String merchantName;

    /**
     * 用户名
     * 操作员的登录用户名，全局唯一
     */
    private String username;

    // ============== 认证信息 ==============
    /**
     * 密码
     * 操作员的登录密码，加密存储
     */
    private String password;

    /**
     * 验证码
     * Google验证码，用于二次验证
     */
    private String googleCode;

    // ============== 安全信息 ==============
    /**
     * 最近密码更新时间
     * 记录密码最后修改时间，用于密码策略控制
     */
    private Long lastPasswordUpdateTime;

    // ============== 状态控制 ==============
    /**
     * 状态
     * true: 启用
     * false: 禁用
     */
    private boolean status;

    // ============== 扩展属性 ==============
    /**
     * 扩展字段
     * JSON格式，用于存储其他扩展信息
     */
    private String attribute;
}
