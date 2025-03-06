package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum WithdrawPeriodEnum {

    MANUAL, //按手工
    AMOUNT, //按金额
    DAY,  //按时间-每日 00：30：00 提现
    WEEK, //按时间-每周
    MONTH; //按时间-每月


    /**
     * codeToEnum
     */
    public static WithdrawPeriodEnum nameToEnum(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return Arrays.stream(WithdrawPeriodEnum.values())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElse(null);
    }

}
