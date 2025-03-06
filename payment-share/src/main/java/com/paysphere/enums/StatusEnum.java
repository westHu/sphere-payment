package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {

    YES(1, "有效"),
    NO(0, "无效");

    private final Integer code;
    private final String name;
}
