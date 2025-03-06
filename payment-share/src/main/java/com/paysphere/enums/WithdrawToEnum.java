package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum WithdrawToEnum {

    PAYIN_ACCOUNT,  //默认
    PAYOUT_ACCOUNT, //代付账户
    BANK_ACCOUNT,   //提现银行
    USDT_ADDRESS;   //提现USDT


    /**
     * nameToEnum
     */
    public static WithdrawToEnum nameToEnum(String name) {
        if (StringUtils.isBlank(name)) {
            return PAYIN_ACCOUNT;
        }
        return Arrays.stream(WithdrawToEnum.values())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElse(PAYIN_ACCOUNT);
    }
}
