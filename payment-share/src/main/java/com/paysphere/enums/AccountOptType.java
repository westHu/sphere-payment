package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountOptType {

    SETTLEMENT("结算"),
    CASH("出款"),
    FROZEN("冻结"),
    UNFREEZE("解冻"),
    ;


    private final String name;

}
