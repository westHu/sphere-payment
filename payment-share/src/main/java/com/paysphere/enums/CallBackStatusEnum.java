package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum CallBackStatusEnum {

    CALLBACK_FAILED(-1, "FAILED"),
    CALLBACK_TODO(0, "READY"),
    CALLBACK_SUCCESS(1, "SUCCESS");

    private final Integer code;
    private final String name;

    /**
     * CallBackStatusEnum
     */
    public static CallBackStatusEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return CALLBACK_FAILED;
        }
        return Arrays.stream(CallBackStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(CALLBACK_FAILED);
    }
}
