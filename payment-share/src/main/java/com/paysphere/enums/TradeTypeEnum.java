package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum TradeTypeEnum {

    PAYMENT(1, "收款", PaymentDirectionEnum.TRANSACTION),
    PAYOUT(2, "出款", PaymentDirectionEnum.DISBURSEMENT),
    RECHARGE(3, "充值", PaymentDirectionEnum.TRANSACTION),
    TRANSFER(4, "转账", PaymentDirectionEnum.TRANSFER),
    WITHDRAW(5, "提现", PaymentDirectionEnum.DISBURSEMENT),
    REFUND(6, "退款", PaymentDirectionEnum.REFUND),
    PAYMENT_LINK(7, "支付链接", PaymentDirectionEnum.TRANSACTION),
    UNKNOWN(99, "未知", PaymentDirectionEnum.UNKNOWN),
    ;

    private final Integer code;
    private final String name;
    private final PaymentDirectionEnum paymentDirection;


    /**
     * codeToEnum
     */
    public static TradeTypeEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(TradeTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }


    /**
     * 通过订单号来判断订单类型
     */
    public static TradeTypeEnum tradeNoToTradeType(String tradeNo) {
        return Optional.ofNullable(tradeNo).map(e -> e.substring(1, 2))
                .map(Integer::parseInt)
                .map(TradeTypeEnum::codeToEnum)
                .orElse(TradeTypeEnum.UNKNOWN);
    }
}
