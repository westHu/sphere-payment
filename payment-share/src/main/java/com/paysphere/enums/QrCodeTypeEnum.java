package com.paysphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum QrCodeTypeEnum {


    DYNAMIC_CODE(0, "DYNAMIC_CODE"),
    STATIC_CODE(1,"STATIC_CODE"),
    UNKNOWN(99,"UNKNOWN")
    ;

    private final Integer code;
    private final String name;

    public static QrCodeTypeEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(QrCodeTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }

}