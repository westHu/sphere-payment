package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum AccountOptTypeEnum {


    IMMEDIATE_SETTLE("即刻结算"),
    PRE_SETTLE("结算前置"),
    DELAYED_SETTLE("延迟结算"),

    CASH("出款"),
    FROZEN("冻结"),
    UNFROZEN("解冻"),

    TRANSFER("转账"),
    TRANSFER_OUT("转账转出"),
    TRANSFER_TO("转账转入"),

    REFUND("退款"),
    REFUND_SETTLE("结算退款"),
    REFUND_TO_SETTLE("待结算退款"),

    RECHARGE("充值"),
    WITHDRAW("提现"),
    ;


    private final String name;


    /**
     * 结算流水
     */
    public static List<AccountOptTypeEnum> needSettlementFlow() {
        return new ArrayList<>(Arrays.asList(IMMEDIATE_SETTLE, DELAYED_SETTLE));
    }

}
