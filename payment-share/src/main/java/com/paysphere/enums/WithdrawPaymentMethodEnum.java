package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum WithdrawPaymentMethodEnum {

    //数字货币
    USDT(DigitalEnum.USDT.getCode(), "USDT"),
    USDC(DigitalEnum.USDC.getCode(), "USDC"),
    BTC(DigitalEnum.BTC.getCode(), "BTC"),
    ETH(DigitalEnum.ETH.getCode(), "ETH"),
    BNB(DigitalEnum.BNB.getCode(), "BNB"),

    //地区法币
    BANK(AreaEnum.INDONESIA.getCode(), "BANK"),

    PAYOUT_BANK(AreaEnum.INDIA.getCode(), "PAYOUT_BANK"),

    PAYOUT_BRAZIL(AreaEnum.BRAZIL.getCode(), "PAYOUT_BRAZIL"),

    PAYOUT_MEXICO(AreaEnum.MEXICO.getCode(), "PAYOUT_MEXICO"),

    PAYOUT_THAILAND(AreaEnum.THAILAND.getCode(), "PAYOUT_THAILAND");

    private final Integer code;
    private final String paymentMethod;

    /**
     * 到银行的支付方式
     */
    public static List<String> needWithdrawPaymentMethodList(Integer code) {
        if (Objects.isNull(code)) {
            return Arrays.stream(WithdrawPaymentMethodEnum.values())
                    .map(WithdrawPaymentMethodEnum::getPaymentMethod)
                    .collect(Collectors.toList());
        }

        return Arrays.stream(WithdrawPaymentMethodEnum.values())
                .filter(e -> code.equals(e.code))
                .map(WithdrawPaymentMethodEnum::getPaymentMethod)
                .collect(Collectors.toList());
    }

}


