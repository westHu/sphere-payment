package com.paysphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ManualFrozenStatusEnum {
    UNFROZEN_SUCCESS(0, "解冻成功"),
    FROZEN_SUCCESS(1, "冻结成功"),
    UNKNOWN(99, "未知");

    private final Integer code;
    private final String name;

    /**
     * codeToEnum
     */
    public static ManualFrozenStatusEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(ManualFrozenStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }
}
