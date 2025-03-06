package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum DeductionTypeEnum {


    DEDUCTION_INTERNAL(0, "内扣"),
    DEDUCTION_EXTERNAL(1, "外扣"),

    UNKNOWN(99, "未知");

    private final Integer code;
    private final String name;

    /**
     * codeToEnum
     */
    public static DeductionTypeEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(DeductionTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }

}
