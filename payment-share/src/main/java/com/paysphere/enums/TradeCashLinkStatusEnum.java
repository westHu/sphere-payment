package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 支付链接状态
 */
@Getter
@AllArgsConstructor
public enum TradeCashLinkStatusEnum {

    CASH_LINK_INIT(0, "初始化"),
    CASH_LINK_PROCESSING(2, "交易中"),
    CASH_LINK_SUCCESS(1, "交易成功"),
    CASH_LINK_FAILED(-1, "交易失败"),
    CASH_LINK_OVER_TIME(-2, "已过期"),

    UNKNOWN(99, "未知");

    private final Integer code;
    private final String name;


    /**
     * TradeLinkStatusEnum
     */
    public static TradeCashLinkStatusEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(TradeCashLinkStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }


}
