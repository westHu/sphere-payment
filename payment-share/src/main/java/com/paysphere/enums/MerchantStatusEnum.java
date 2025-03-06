package com.paysphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 商户状态
 * 未激活/正常/冻结/休眠/注销...
 */
@Getter
@AllArgsConstructor
public enum MerchantStatusEnum {

    PENDING_ACTIVE(0, "待激活"),
    PENDING_CONFIG(1, "待进件"),
    PENDING_EXAMINE(4, "待流程审核"),

    NORMAL(10, "正常"),
    ABNORMAL(11, "异常"),

    FROZEN(21, "冻结"),
    DORMANT(22, "休眠"),
    CANCEL(23, "注销"),

    UNKNOWN(99, "未知");


    private final Integer code;
    private final String name;


    public static MerchantStatusEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(MerchantStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny().orElse(UNKNOWN);
    }

}
