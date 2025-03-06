package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum MerchantQuerySourceEnum {

    MERCHANT_ADMIN(1), ADMIN(2);

    private final Integer code;

    /**
     * codeToEnum
     */
    public static MerchantQuerySourceEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return MERCHANT_ADMIN;
        }
        return Arrays.stream(MerchantQuerySourceEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(MERCHANT_ADMIN);
    }
}
