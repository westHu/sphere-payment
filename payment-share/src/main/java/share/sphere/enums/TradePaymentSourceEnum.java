package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TradePaymentSourceEnum {

    API(0), PAY_LINK(1), UNKNOWN(99);

    private final Integer code;

    /**
     * codeToEnum
     */
    public static TradePaymentSourceEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(TradePaymentSourceEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }

}
