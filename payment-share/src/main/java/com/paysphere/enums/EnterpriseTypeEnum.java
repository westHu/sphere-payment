package com.paysphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnterpriseTypeEnum {

    INCORPORATION(1, "注册成立"),
    PARTNERSHIP(2, "有限合伙企业"),
    GOVERNMENT(3, "政府"),
    FOREIGN_ENTITIES(4, "外国实体"),
    SOLE_PROPRIETORSHIP(5, "独资企业"),
    BASICS(6, "基础"),
    COOPERATIVE(7, "合作社"),

    OTHERS(99, "其他");

    private final Integer code;
    private final String name;

}
