package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeOrderOptType {

    SUPPLEMENT("supplement"),// 补单
    REFUND("refund"); // 退单

    private final String name;
}
