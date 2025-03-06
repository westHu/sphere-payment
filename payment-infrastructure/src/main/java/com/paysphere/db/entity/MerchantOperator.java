package com.paysphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商户操作员
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant_operator")
public class MerchantOperator extends BaseEntity {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 角色
     */
    private Long role;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 交易密码
     */
    private String tradePassword;

    /**
     * 最近密码更新时间
     */
    private LocalDateTime lastPasswordUpdateTime;

    /**
     * 最近交易密码更新时间
     */
    private LocalDateTime lastTradePasswordUpdateTime;

    /**
     * 验证码
     */
    private String googleCode;

    /**
     * 谷歌授权登录
     */
    private String googleEmail;

    /**
     * 状态
     */
    private boolean status;

    /**
     * 扩展字段
     */
    private String attribute;
}
