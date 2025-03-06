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
public enum TradePaymentLinkStatusEnum {

    PAYMENT_LINK_INIT(0, "初始化"),
    PAYMENT_LINK_PROCESSING(2, "交易中"),
    PAYMENT_LINK_SUCCESS(1, "交易成功"),
    PAYMENT_LINK_FAILED(-1, "交易失败"),
    PAYMENT_LINK_EXPIRED(-2, "已过期"),

    UNKNOWN(99, "未知");

    private final Integer code;
    private final String name;


    /**
     * TradeLinkStatusEnum
     */
    public static TradePaymentLinkStatusEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(TradePaymentLinkStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }


}
