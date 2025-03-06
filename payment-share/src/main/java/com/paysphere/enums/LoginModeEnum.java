package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum LoginModeEnum {

    PASSWORD("password登录"),
    GOOGLE("google授权登录"),
    FACEBOOK("facebook授权登录"),
    TELEGRAM("telegram谷歌授权登录"),
    UNKNOWN("未知"),
    ;

    private final String name;


    /**
     * codeToEnum
     */
    public static LoginModeEnum codeToEnum(String name) {
        if (Objects.isNull(name)) {
            return UNKNOWN;
        }
        return Arrays.stream(LoginModeEnum.values())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElse(UNKNOWN);
    }

}
