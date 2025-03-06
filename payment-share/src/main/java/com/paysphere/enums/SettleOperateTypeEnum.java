package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SettleOperateTypeEnum {

    FROZEN("frozen"),
    UNFROZEN("unfrozen"),
    ADVANCE("advance"),
    RESETTLEMENT("resettlement"),

    UNKNOWN("unknown");

    private final String name;
}
