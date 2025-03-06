package com.paysphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AccountRoleEnum {


    DEFAULT(0, "默认账户"),
    OTHER(1, "其他账户");


    private final Integer code;
    private final String name;

    public static String codeToName(Integer code) {
        if (Objects.isNull(code)) {
            return OTHER.getName();
        }
        return Arrays.stream(AccountRoleEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny().orElse(OTHER).getName();
    }

    public static Integer nameToCode(String name) {
        if (Objects.isNull(name)) {
            return OTHER.getCode();
        }
        return Arrays.stream(AccountRoleEnum.values())
                .filter(e -> e.getName().equals(name))
                .findAny().orElse(OTHER).getCode();
    }
}
