package com.paysphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 地区
 *
 * @author West.Hu
 */
@Getter
@AllArgsConstructor
public enum DigitalEnum {

    USDT(30, CurrencyEnum.USDT),
    USDC(31, CurrencyEnum.USDC),
    BTC(32, CurrencyEnum.BTC),
    ETH(33, CurrencyEnum.ETH),
    BNB(34, CurrencyEnum.BNB),
    TRX(35, CurrencyEnum.TRX),
    ;


    private final Integer code;
    private final CurrencyEnum currency;


    /**
     * codeToEnum
     */
    public static DigitalEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return USDT;
        }
        return Arrays.stream(DigitalEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(USDT);
    }

    /**
     * 多个Code 转 数字
     */
    public static Integer digitalListToDigital(List<Integer> digitalList) {
        if (CollectionUtils.isEmpty(digitalList)) {
            return null;
        }

        String extractUnitDigits = digitalList.stream()
                .sorted()
                .map(num -> num % 10)
                .map(String::valueOf)
                .collect(Collectors.joining());
        String digitalStr = "3" + extractUnitDigits;
        return Integer.parseInt(digitalStr);
    }


    /**
     * 数字 转化成为 多个Code
     * 30123
     */
    public static List<DigitalEnum> digitalToDigitalEnumList(Integer digital) {
        if (Objects.isNull(digital)) {
            return new ArrayList<>();
        }
        String numberString = Integer.toString(digital).substring(1);
        List<DigitalEnum> digitalEnumList = new ArrayList<>();
        for (char c : numberString.toCharArray()) {
            String digitalStr = "3" + c;
            int digitalInt = Integer.parseInt(digitalStr);
            DigitalEnum digitalEnum = codeToEnum(digitalInt);
            digitalEnumList.add(digitalEnum);
        }
        return digitalEnumList;
    }


    public static DigitalEnum paymentToEnum(String paymentMethod) {
        if (Objects.isNull(paymentMethod)) {
            return null;
        }
        return Arrays.stream(DigitalEnum.values())
                .filter(e -> e.getCurrency().name().equalsIgnoreCase(paymentMethod))
                .findAny()
                .orElse(null);
    }
    public static Boolean isDigitalCurrency(String paymentMethod) {
        if (Objects.isNull(paymentMethod)) {
            return false;
        }
        return EnumSet.allOf(DigitalEnum.class).contains(paymentToEnum(paymentMethod));
    }

}
