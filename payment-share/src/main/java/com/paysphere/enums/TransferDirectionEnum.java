package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferDirectionEnum {

    OUT(1, "转出"), TO(2, "转入");

    private final Integer code;
    private final String name;
}
