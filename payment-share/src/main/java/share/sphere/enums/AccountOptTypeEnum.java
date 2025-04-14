package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter
@AllArgsConstructor
public enum AccountOptTypeEnum {


    IMMEDIATE_SETTLE("即刻结算"),
    PRE_SETTLE("结算前置"),
    DELAYED_SETTLE("延迟结算"),

    PAYOUT("出款结算"),

    FROZEN("冻结"),
    UNFROZEN("解冻"),

    TRANSFER("转账"),

    REFUND("退款"),

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
