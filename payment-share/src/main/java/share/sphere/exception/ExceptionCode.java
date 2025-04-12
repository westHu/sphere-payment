package share.sphere.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 * 错误码设计规则：
 * 1. 六位数字，前两位表示主模块，中间一位表示子模块，后三位表示具体错误码
 * 2. 主模块划分：
 *    10-19: 系统级错误
 *    20-29: 参数校验错误
 *    30-39: 认证授权错误
 *    40-49: 商户相关错误
 *    50-59: 支付相关错误
 *    60-69: 风控相关错误
 *    70-79: 预留
 *    80-89: 预留
 *    90-99: 预留
 * 3. 每个主模块预留足够的子模块空间
 */
@Getter
@AllArgsConstructor
public enum ExceptionCode {

    SUCCESS(200, "成功"),

    // 系统级错误 (10xxxx)
    SYSTEM_ERROR(100001, "系统内部错误"),
    SYSTEM_BUSY(100002, "系统繁忙"),
    SYSTEM_MAINTENANCE(100003, "系统维护中"),
    SYSTEM_CONFIG_ERROR(110001, "系统配置错误"),
    SYSTEM_RESOURCE_ERROR(120001, "系统资源错误"),

    // 参数校验错误 认证授权错误 (20xxxx)
    PARAM_ERROR(200001, "参数错误"),
    PARAM_MISSING(200002, "参数缺失"),
    PARAM_FORMAT_ERROR(200003, "参数格式错误"),
    REQUEST_PARAM_ERROR(210001, "请求参数错误"),
    BUSINESS_PARAM_ERROR(220001, "业务参数错误"),
    AUTH_ERROR(200001, "认证失败"),
    AUTH_EXPIRED(200002, "认证已过期"),
    AUTH_INVALID(200003, "无效的认证信息"),
    PERMISSION_DENIED(210001, "权限不足"),
    TOKEN_EXPIRED(220001, "令牌已过期"),
    TOKEN_INVALID(220002, "无效的令牌"),

    // 用户相关错误 (30xxxx)
    USER_NOT_FOUND(300001, "用户不存在: {1}"),
    USER_DISABLED(300002, "用户已禁用: {1}"),
    USER_LOGIN_ERROR(300003, "用户名或密码错误: {1}"),
    USER_HAS_EXIST(300004, "用户已存在: {1}"),

    // 请结算 账户相关
    SETTLE_ACCOUNT_HAS_EXIST(220002, "账户已存在:{1}"),

    // 商户相关错误 (40xxxx)
    MERCHANT_NOT_FOUND(400001, "商户不存在"),
    MERCHANT_HAS_EXIST(400001, "商户已存在"),
    MERCHANT_DISABLED(400002, "商户已禁用"),
    MERCHANT_LOGIN_ERROR(400002, "商户登录失败: {1}"),
    MERCHANT_LOGIN_ERROR2(400002, "todo 账户密码错误: {1}"),
    MERCHANT_LOGIN_ERROR3(400002, "todo 登录验证码错误: {1}"),
    MERCHANT_LOGIN_ERROR4(400002, "todo 原密码验证失败: {1}"),
    MERCHANT_CONFIG_ERROR(400003, "商户配置错误"),
    MERCHANT_CONFIG_NOT_EXIST(400004, "商户配置不存在: {1}"),
    MERCHANT_INFO_ERROR(410001, "商户基础信息错误"),
    MERCHANT_CONFIG_MISSING(420001, "商户配置缺失"),

    // 支付相关错误 (50xxxx)
    PAYMENT_CONFIG_NOT_FOUNT(500001, "支付配置缺失"),
    PAYMENT_CONFIG_DISABLED(500001, "支付配置已禁用"),
    PAYMENT_CONFIG_HAS_EXIST(500001, "支付配置已存在"),
    PAYMENT_ERROR(500001, "支付失败"),
    PAYMENT_TIMEOUT(500002, "支付超时"),
    PAYMENT_CANCELLED(500003, "支付已取消"),
    PAYMENT_PROCESS_ERROR(510001, "支付处理错误"),
    PAYMENT_CHANNEL_ERROR(520001, "支付渠道错误"),
    PAYMENT_CALLBACK_ERROR(520001, "支付回调错误"),

    // 交易相关错误 (60xxxx)
    TRADE_ORDER_NOT_FOUND(600001, "订单不存在"),
    TRADE_ORDER_NOT_FINAL(600001, "风控拦截"),
    TRADE_ORDER_HAS_FAILED(600001, "风控拦截"),
    TRADE_ORDER_HAS_EXPIRED(600001, "风控拦截"),
    TRADE_CALLBACK_ERROR(600002, "风控规则错误"),
    RISK_CHECK_FAILED(600003, "风控检查未通过"),
    RISK_RULE_MISSING(610001, "风控规则缺失"),
    RISK_INTERCEPT_ERROR(620001, "风控拦截错误");

    private final Integer code;
    private final String message;

}