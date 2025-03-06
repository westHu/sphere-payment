package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TradeModeEnum {

    SANDBOX("sandbox"), PRODUCTION("production"), UNKNOWN("UNKNOWN");

    private final String mode;

    /**
     * codeToEnum
     */
    public static TradeModeEnum codeToEnum(String mode) {
        if (Objects.isNull(mode)) {
            return UNKNOWN;
        }
        return Arrays.stream(TradeModeEnum.values())
                .filter(e -> e.getMode().equals(mode))
                .findAny()
                .orElse(UNKNOWN);
    }
}
