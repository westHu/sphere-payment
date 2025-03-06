package com.paysphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 地区
 *
 * @author West.Hu
 */
@Getter
@AllArgsConstructor
public enum AreaEnum {

    /**
     * countryId 不适用订单编码组成，不然订单意图太明显
     */
    //地区
    INDONESIA(10, CurrencyEnum.IDR, "印尼",62),
    THAILAND(11, CurrencyEnum.THB, "泰国", 66),
    INDIA(12, CurrencyEnum.INR, "印度", 91),
    BRAZIL(13, CurrencyEnum.BRL, "巴西", 55),
    MEXICO(14, CurrencyEnum.MXN, "墨西哥", 52),

    //数字货币
    USDT(DigitalEnum.USDT.getCode(), DigitalEnum.USDT.getCurrency(), DigitalEnum.USDT.name(), 62),
    USDC(DigitalEnum.USDC.getCode(), DigitalEnum.USDC.getCurrency(), DigitalEnum.USDC.name(), 62),
    BTC(DigitalEnum.BTC.getCode(), DigitalEnum.BTC.getCurrency(), DigitalEnum.BTC.name(), 62),
    ETH(DigitalEnum.ETH.getCode(), DigitalEnum.ETH.getCurrency(), DigitalEnum.ETH.name(), 62),
    BNB(DigitalEnum.BNB.getCode(), DigitalEnum.BNB.getCurrency(), DigitalEnum.BNB.name(), 62),
    TRX(DigitalEnum.TRX.getCode(), DigitalEnum.TRX.getCurrency(), DigitalEnum.TRX.name(), 62),

    UNKNOWN(0, null, "未知", 0);




    ;

    private final Integer code;
    private final CurrencyEnum currency;
    private final String name;
    private final Integer countryId;


    /**
     * codeToEnum
     */
    public static AreaEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return INDONESIA;
        }
        return Arrays.stream(AreaEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(INDONESIA);
    }

    public static Integer getCodeByName(String name){
        return Arrays.stream(AreaEnum.values())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElse(UNKNOWN).getCode();
    }


}
