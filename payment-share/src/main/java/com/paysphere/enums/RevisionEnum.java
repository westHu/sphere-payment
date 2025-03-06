package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 校验情况
 */
@Getter
@AllArgsConstructor
public enum RevisionEnum {

    UNDO(0, "都未校验"), //0000
    Merchant_DONE(1, "商户校验完成"), //0001
    PLATFORM_DONE(2, "平台校验完成"), //0010
    DONE(3, "都校验完成"), //0011
    UNKNOWN(99, "未知");

    private final Integer code;
    private final String name;


    /**
     * codeToEnum
     */
    public static RevisionEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(RevisionEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }
}
