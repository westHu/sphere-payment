package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MerchantBaseConfigEnum {

    WITHDRAW_EXCHANGE_RATE("提现兑换率"),
    RECHARGE_EXCHANGE_RATE("充值兑换率");

    private final String name;

}
