package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotifyTypeEnum {


    ALL(1, "全部"),
    COUNTRY(2, "国家"),
    MERCHANT_TYPE(3, "商户类型"),
    MERCHANT_ID(4, "商户ID");

    private final Integer code;
    private final String name;
}
