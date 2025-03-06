package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置类型
 */
@Getter
@AllArgsConstructor
public enum SettleConfigTypeEnum {


    SETTLEMENT_SWITCH("结算开关"), UNKNOWN("未知");

    private final String name;

}
