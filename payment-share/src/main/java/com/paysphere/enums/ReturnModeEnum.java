package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ReturnModeEnum {

    CASHIER_MODE(0, "cashier"),//收银台
    NUMBER_MODE(1, "number"),//收银号
    BOTH_MODE(4, "both"),//2者都有

    UNKNOWN(99, "unknown");//未知

    private final Integer code;
    private final String name;

    /**
     * 根据code查询状态
     *
     * @param code
     * @return
     */
    public static String codeToName(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN.getName();
        }
        return Arrays.stream(ReturnModeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN)
                .getName();
    }

    /**
     * 根据code查询状态
     *
     * @param name
     * @return
     */
    public static Integer nameToCode(String name) {
        if (StringUtils.isBlank(name)) {
            return UNKNOWN.getCode();
        }
        return Arrays.stream(ReturnModeEnum.values())
                .filter(e -> e.getName().equals(name))
                .map(ReturnModeEnum::getCode)
                .findAny().orElse(UNKNOWN.getCode());
    }

}
