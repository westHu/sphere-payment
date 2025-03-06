package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 转账方向
 */
@Getter
@AllArgsConstructor
public enum TradeTransferDirectionEnum {

    TRANSFER_OUT(-1, "转出"),
    TRANSFER_IN(1, "转入"),

    UNKNOWN(99, "未知");

    private final Integer code;
    private final String name;



}
