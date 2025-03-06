package com.paysphere.utils;


import com.paysphere.TradeConstant;
import com.paysphere.enums.AreaEnum;

public class AccountUtil {


    /**
     * 解析地区平台商户
     */
    public static String getPlatformMerchantId() {
        return AreaEnum.INDONESIA.getCode() + TradeConstant.PLATFORM_MERCHANT_CODE_PADDING;
    }

    /**
     * 解析地区平台商户名称
     */
    public static String getPlatformMerchantName() {
        return TradeConstant.PLATFORM_MERCHANT_NAME_PADDING;
    }

    /**
     * 解析地区平台商户账户
     */
    public static String getPlatformMerchantAccountNo() {
        return getPlatformMerchantId() + TradeConstant.PLATFORM_MERCHANT_ACCOUNT_PADDING;
    }

}
