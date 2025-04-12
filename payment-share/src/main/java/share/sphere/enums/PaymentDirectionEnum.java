package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 支付模式
 */
@Getter
@AllArgsConstructor
public enum PaymentDirectionEnum {

    /**
     * 数据库存储，二进制法
     * 收款 0001
     * 出款 0010
     * 转账 0100
     * ...
     * <p>
     * 如果某个渠道支持  收款、出款 那么状态等于 0011 = 3
     * 那么如果要查询能出款的渠道，只要转成二进制，看第一位是不是1即可
     * 可读性差，但效果好
     */

    TRANSACTION(1, "收款", TradeTypeEnum.PAYMENT),
    DISBURSEMENT(2, "出款", TradeTypeEnum.PAYOUT),
    TRANSFER(4, "转账", TradeTypeEnum.TRANSFER),
    REFUND(8, "退款", TradeTypeEnum.REFUND);


    private final Integer code;
    private final String name;
    private final TradeTypeEnum tradeTypeEnum;


    public static PaymentDirectionEnum codeToEnum(Integer code) {
        return Arrays.stream(PaymentDirectionEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(TRANSACTION);
    }

}
