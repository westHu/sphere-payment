package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum PaymentStatusEnum {

    PAYMENT_PENDING(0, "待支付"),
    PAYMENT_PROCESSING(2, "支付中"),

    PAYMENT_SUCCESS(1, "支付成功"),

    PAYMENT_FAILED(-1, "支付失败"),
    PAYMENT_FAILED_TT(-2, "超时失败"),

    UNKNOWN(99, "未知");

    private final Integer code;
    private final String name;

    public static PaymentStatusEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(PaymentStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }

    public static List<Integer> getFinalStatus() {
        return Stream.of(PaymentStatusEnum.PAYMENT_SUCCESS,
                        PaymentStatusEnum.PAYMENT_FAILED,
                        PaymentStatusEnum.PAYMENT_FAILED_TT)
                .map(PaymentStatusEnum::getCode)
                .collect(Collectors.toList());
    }

}
