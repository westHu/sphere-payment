package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ApiModeEnum {

    API(1, "API模式"),
    CASHIER(2, "收银台模式"),
    UNKNOWN(99, "未知"),
    ;

    private final Integer code;
    private final String name;


    /**
     * codeToEnum
     */
    public static ApiModeEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(ApiModeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }

    /**
     * 默认接入模式
     */
    public static List<Integer> defaultApiModeList() {
        return Arrays.asList(API.getCode(), CASHIER.getCode());
    }

}
