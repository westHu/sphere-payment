package com.paysphere.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    /**
     * 成功
     */
    SUCCESS(200, "Request Success"),
    FAILED(500, "Request Failed"),

    // 参数
    PARAM_IS_REQUIRED(2000, "Parameter [{1}] is required"),
    PARAM_IS_NOT_CONFIG(2001, "Parameter [{1}] is not config"),
    PARAM_IS_UNSUPPORTED(2002, "Parameter [{1}] is unsupported"),
    PARAM_IS_INVALID(2002, "Parameter invalid: {1}"),


    // 消息
    MESSAGE_MQ_ERROR(2005, "Message send to mq error. refer to [{1}]"),
    MESSAGE_CONSUMER_LATER(2008, "{1} consumer later."),

    /**
     * 沙箱代付
     */
    SANDBOX_CASH_ORDER_REPEAT(2101, "Sandbox payout order [{1}] already exists."),
    SANDBOX_CASH_ORDER_NOT_EXIST(2102, "Sandbox payout order [{1}] does not exist."),
    SANDBOX_CASH_ORDER_TRANSACTION_FAILED(2104, "Sandbox payout order [{1}] transaction failed."),
    SANDBOX_CASH_ORDER_FINAL_STATUS(2105, "Sandbox payout order [{1}] has already final status."),
    SANDBOX_CASH_CONFIG_NOT_EXIST(2106, "Sandbox merchant [{1}] payout config does not exist."),

    /**
     * 代付
     */
    CASH_ORDER_REPEAT(2110, "Payout order [{1}] already exists."),
    CASH_ORDER_NOT_EXIST(2111, "Payout order [{1}] does not exist."),
    CASH_ORDER_NOT_IN_REVIEW(2112, "Payout order [{1}] not in review status."),
    CASH_ORDER_FINAL_STATUS(2113, "Payout order [{1}] already final status."),
    CASH_ORDER_NOT_FINAL_STATUS(2114, "Payout order [{1}] not final status."),
    CASH_TOO_MANY_QUANTITIES(2115, "Payout too many quantities. maybe less than [{1}]."),
    CASH_TOTAL_AMOUNT_VERIFY_FAILED(2116, "Payout failed to verify total amount."),
    CASH_CONFIG_NOT_EXIST(2117, "Merchant [{1}] payout config does not exist."),
    CASH_CHANNEL_CONFIG_EMPTY(2118, "Merchant [{1}] payout channel config is empty."),
    CASH_ACCOUNT_NOT_EXIST(2129, "Payout account [{1}] not exist."),
    CASH_BALANCE_NOT_ENOUGH(2121, "Payout account [{1}] balance [{2}] is not enough, payout amount: [{3}]"),
    CASH_FROZEN_AMOUNT_FAILED(2122, "Payout order [{1}]. Maybe no enough balance. Rp{2}"),
    CASH_UNFROZEN_AMOUNT_FAILED(2123, "Payout order [{1}] failed to unfrozen amount."),
    CASH_CALCULATE_FEE_FAILED(2124, "Payout order [{1}] failed to calculate fee amount."),
    CASH_ORDER_TRANSACTION_FAILED(2126, "Payout order [{1}] transaction not success."),
    CASH_ORDER_PAYMENT_SUCCESS(2127, "Payout order [{1}] already payment success."),
    CASH_ORDER_PAYMENT_FAILED(2128, "Payout order [{1}] already payment failed."),
    CASH_CHANNEL_ERROR(2143, "Payout order [{1}] failed. [{2}]."),
    CASH_HAS_SUPPLEMENT(2145, "Payout order [{1}] has supplement."),
    CASH_LINK_NOT_EXIST(2154, "Payout link [{1}] not exist."),
    CASH_ACTUAL_AMOUNT_ERROR(2166, "Payout actual money less than zero. Rp {1}"),
    CASH_ACCOUNT_NO_NOT_MATCH(2169, "Payout merchant accountNo does not match. {1}. {2}"),


    /**
     * 沙箱收款
     */
    SANDBOX_PAY_ORDER_REPEAT(2201, "Sandbox payin order [{1}] already exists."),
    SANDBOX_PAY_ORDER_NOT_EXIST(2201, "Sandbox payin order [{1}] does not exist."),
    SANDBOX_PAY_ORDER_HAS_FAILED(2202, "Sandbox payin order [{1}] already failed."),
    SANDBOX_PAY_ORDER_HAS_EXPIRED(2203, "Sandbox order [{1}] has already expired."),
    SANDBOX_PAY_ORDER_TRANSACTION_FAILED(2204, "Sandbox payin order [{1}] transaction not success."),
    SANDBOX_PAY_ORDER_FINAL_STATUS(2205, "Sandbox payin order [{1}] has already final status."),
    SANDBOX_PAY_CONFIG_NOT_EXIST(2206, "Merchant [{1}], sandbox payin config not exist."),

    /**
     * 收款
     */
    PAY_ORDER_REPEAT(2207, "Payin order [{1}] has already exists."),
    PAY_ORDER_NOT_EXIST(2208, "Payin order [{1}] does not exist."),
    PAY_ORDER_FINAL_STATUS(2209, "Payin order [{1}] has already final status."),
    PAY_ORDER_NOT_FINAL_STATUS(2210, "Payin order [{1}] not final status."),
    PAY_ORDER_HAS_FAILED(2213, "Payin order [{1}] has already failed."),
    PAY_ORDER_HAS_EXPIRED(2214, "Payin order [{1}] has already expired."),
    PAY_ORDER_STATUS_INVALID(2215, "Payin order [{1}] status is invalid."),
    PAY_CONFIG_NOT_EXIST(2216, "Merchant [{1}] payin config not exist."),
    PAY_CHANNEL_CONFIG_EMPTY(2217, "Merchant [{1}] payin channel config is empty."),
    PAY_ORDER_TRANSACTION_FAILED(2221, "Payin order [{1}] transaction not success."),
    PAY_ORDER_PAYMENT_SUCCESS(2222, "Payin order [{1}] already payment success."),
    PAY_ORDER_PAYMENT_FAILED(2223, "Payin order [{1}] already payment failed."),
    PAY_CHANNEL_ERROR(2228, "Payin [{1}] failed. [{2}]."),
    PAY_HAS_SUPPLEMENT(2220, "Payin order [{1}] has supplement."),
    PAY_INVALID_FEE_CONFIG(2222, "Payin order [{1}] invalid param merchant fee config."),
    PAY_INVALID_TOKEN(2222, "Payin order [{1}] payment url invalid token"),
    PAY_CHANNEL_TIMEOUT(2226, "Payin order [{1}] channel socket timeout. Try it again"),


    /**
     * 转账
     */
    TRANSFER_ORDER_NOT_EXIST(2301, "Transfer order [{1}] does not exist."),
    TRANSFER_WITH_SAME_ACCOUNT(2302, "Transfer failed to transfer with same accountNo [{1}]."),
    TRANSFER_FROZEN_AMOUNT_FAILED(2303, "Transfer order [{1}] failed to frozen amount."),
    TRANSFER_ORDER_NOT_IN_REVIEW(2305, "Transfer order [{1}] not in review status."),
    TRANSFER_CHANNEL_ERROR(2307, "Transfer order [{1}] failed. [{2}]"),

    /**
     * 充值
     */
    RECHARGE_ORDER_NOT_EXIST(2311, "Recharge order [{1}] does not exist."),
    RECHARGE_ORDER_NOT_IN_REVIEW(2315, "Recharge order [{1}] not in [REVIEW]."),
    RECHARGE_UNSUPPORTED_PAYMENT(2316, "Recharge unsupported payment [{1}]"),
    RECHARGE_ALREADY_SUCCESS(2319, "Recharge order [{1}] already [REVIEW/SUCCESS]."),
    RECHARGE_STATUS_MUST_BE_INIT(2322, "Recharge order [{1}] status must be [INIT]."),
    RECHARGE_CHANNEL_ERROR(2325, "Recharge order [{1}] failed. [{2}]."),
    RECHARGE_MIN_AMOUNT_ERROR(2328, "Recharge amount should more than 10000 IDR."),
    RECHARGE_MAX_AMOUNT_ERROR(2329, "Recharge amount should less than 1000000000 IDR."),

    /**
     * 提现
     */
    WITHDRAW_CONFIG_NOT_EXIST(2417, "Merchant [{1}], withdraw config does not exist."),
    WITHDRAW_FROZEN_AMOUNT_FAILED(2413, "Withdraw order [{1}] failed to frozen amount."),
    WITHDRAW_ORDER_NOT_EXIST(2411, "Withdraw order [{1}] not exist."),
    WITHDRAW_ORDER_NOT_IN_REVIEW(2415, "Withdraw order [{1}] not in [REVIEW]."),
    WITHDRAW_DEDUCTION_NOT_CONFIG(2438, "Merchant [{1}], withdraw merchant deductionType not config."),
    WITHDRAW_CHANNEL_ERROR(2417, "Withdraw order [{1}] failed. [{2}]."),
    WITHDRAW_ACTUAL_AMOUNT_ERROR(2419, "Merchant withdraw actual money less than zero"),

    /**
     * 回调
     */
    CALLBACK_URL_NOT_CONFIG(2401, "Merchant [{1}], callback [URL] not config."),
    CALLBACK_PARAMETER_ERROR(2402, "Order callback parameter validate false. [{1}]"),
    CALLBACK_NOT_ALLOW_TYPE(2404, "Order [{1}] does not right type. can not callback."),

    /**
     * 不支持
     */
    UNSUPPORTED_QUERY_TYPE(2405, "Unsupported merchant query type"),
    UNSUPPORTED_TRADE_TYPE(2406, "Unsupported trade type"),
    UNSUPPORTED_ORDER_TYPE(2407, "Unsupported order type"),
    UNSUPPORTED_DEDUCTION_TYPE(2408, "Unsupported deductionType"),

    /**
     * 业务
     */
    MERCHANT_NOT_EXIST(2409, "Merchant [{1}] does not exist."),
    MERCHANT_STATUS_INVALID(2410, "Merchant [{1}] status invalid."),
    ORDER_STORAGE_FAILED(2413, "Order [{1}] storage failed."),
    ORDER_NUMBER_GENERATION_ERROR(2426, "Order error, order number generation exception"),
    TRADE_MERCHANT_CONFIG_FEE_ERROR(2427, "merchant [{1}] fee rates are not configured"),
    ORDER_NUMBER_GENERATION_BUSY(2709, "Generating order is too busy, try again later"),

    PAYMENT_METHOD_NOT_ACTIVE(4006, "merchant:[{1}], payment:[{2}]. It is inactive"),
    PAYMENT_METHOD_AMOUNT_LIMIT(4007, "merchant:[{1}]. payment:[{2}]. amount limit."),


    PAYMENT_ERROR(3002, "Payment Error: {1}"),
    CHANNEL_ERROR(3003, "Channel Error: {1}"),

    PARAM_INVALID(4003, "Parameter invalid {1}"),
    PARAM_USERNAME_MAX_LENGTH(4004, "Parameter username max length 64-character"),
    PARAM_PASSWORD_MAX_LENGTH(4005, "Parameter password must be 8 to 16 character"),


    //login
    USERNAME_NOT_FOUND(4010, "Login username [{1}] not found."),
    USERNAME_STATUS_INVALID(4019, "Login username status invalid"),
    USERNAME_PASSWORD_INCORRECT(4011, "Login [{1}] incorrect username or password."),
    MERCHANT_HAS_ASSOCIATED(4012, "Login merchant has associated google"),
    GOOGLE_HAS_ASSOCIATED(4013, "Login google account has associated merchant"),
    GOOGLE_VERIFY_FAILED(4014, "Login google verify failed"),
    GOOGLE_TOKEN_INVALID(4015, "Login google token is invalid"),
    GOOGLE_EMAIL_INVALID(4016, "Login google email is invalid"),
    GOOGLE_EMAIL_IS_AVAILABLE(4033, "google email is available: {1}"), //不能动
    GOOGLE_AUTH_ERROR(4018, "Login google auth error. {1}"),


    //email
    EMAIL_CODE_VERIFY_EXCEPTION(4020, "Email OTP failed. refer to [{1}]."),

    //business
    MERCHANT_HAS_EXIST(4025, "Merchant [{1}] has exist."),
    MERCHANT_STATUS_ILLEGAL(4030, "Merchant [{1}] status invalid."),
    MERCHANT_CONFIG_NOT_EXIST(4031, "Merchant [{1}] config not exist."),
    MERCHANT_CONFIG_UPDATE_EXCEPTION(4034, "Merchant config update error"),

    /**
     * INDUSTRY
     */
    INDUSTRY_TEMPLATE_HAS_EXIST(4035, "Industry [{1}] template has exist."),
    INDUSTRY_TEMPLATE_SECOND_LEVEL(4036, "Industry need second level"),

    OPERATOR_NOT_EXIST(4040, "Merchant [{1}] member not exist."),
    CAPTCHA_NOT_EXIST(4041, "captcha not exist"),
    CAPTCHA_VERIFY_FAILED(4042, "Merchant [{1}], captcha verify failed."),
    PASSWORD_OPT_TOO_FREQUENTLY(4043, "merchant reset password too frequently. must exceed 60 minutes"),
    OLD_PASSWORD_IS_WRONG(4044, "Merchant [{1}], old password is wrong."),
    LOGIN_OTP_AUTH_ERROR(4045, "Merchant [{1}], OTP auth failed."),
    AGENT_NOT_EXIST(4046, "Merchant [{1}], invitation code not exist."),
    AGENT_HAS_EXIST(4047, "Merchant [{1}], agent has exist."),
    INVITATION_CODE_HAS_EXIST(4052, "The invitation code has been used by the agent:[{1}]"),
    AGENT_INVALID_STATUS(4048, "Merchant [{1}], invitation status invalid."),
    DEFAULT_ACCOUNT_NOT_EXIST(4049, "Merchant [{1}], default account not exist."),
    APPLY_ACCOUNT_ERROR(4050, "Merchant failed to apply account"),
    DANA_ACCOUNT_HAS_EXIST(4051, "DANA account has exist"),
    APPLY_RECORD_NOT_EXIST(4053, "Merchant account apply record not exist"),
    MEMBER_CONFIRM_TOKEN_INVALID(4053, "Merchant [{1}], member confirm token invalid."),
    OPERATOR_HAS_EXIST(4055, "Merchant [{1}], subMember has already exist."),
    SAME_ACCOUNT_NO(4058, "cannot operate between the same accounts. [{1}]"),
    AGENT_IS_NOT_EXIST(4060, "Partner [{1}] not exist."),
    PARENT_AGENT_IS_NOT_EXIST(4061, "Parent partner [{1}] not exist."),
    AGENT_PASSWORD_IS_WRONG(4064, "Parent [{1}] password is wrong."),
    CHANNEL_APPLY_ERROR(4069, "[{1}] setting is incorrect, please contact customer service"),
    QRIS_CODE_HAS_EXIST(4075, "QRIS ShopName [{1}] has exist."),

    UNSUPPORTED_LOGIN_TYPE(4109, "Unsupported login mode"),
    UNSUPPORTED_MERCHANT_TYPE(4110, "Unsupported merchant type. [{1}]"),
    UNSUPPORTED_CHANNEL(4111, "Unsupported channel. [{1}]"),

    //permission
    MERCHANT_NO_PERMISSION(4115, "No permission to [{1}]. Please apply first!"),

    MERCHANT_NOT_CONFIRM(4120, "Merchant not confirm in email. refer to [{1}]"),
    MERCHANT_EMAIL_TEMPLATE(4121, "Merchant email build content read template exception"),
    MERCHANT_EMAIL_SEND_EXCEPTION(4122, "Merchant email Code Send Error"),
    MERCHANT_POST_NULL_EXCEPTION(4123, "Merchant post error: result is null"),
    MERCHANT_POST_VALIDATE_EXCEPTION(4124, "Merchant post validate no pass"),
    MERCHANT_POST_PAGE_NULL_EXCEPTION(4125, "Merchant post error: {1}"),
    MERCHANT_STATUS_FROZEN(4126, "Merchant [{1}] has been frozen, please contact admin!"),
    MERCHANT_STATUS_DORMANT(4127, "Merchant [{1}] is in hibernation, please contact admin!"),
    MERCHANT_STATUS_CANCEL(4128, "Merchant [{1}] has been cancelled, please contact admin!"),

    //异常
    GENERAL_ERROR(4997, "General error"),
    INTERNAL_SERVER_ERROR(4998, "Internal server error. {1}"),
    EXTERNAL_SERVER_ERROR(4999, "External server error"),
    ;


    private final Integer code;
    private final String message;
}