package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TradePayoutSourceEnum {

    API(0), WEB(1), UNKNOWN(99);

    private final Integer code;

    /**
     * codeToEnum
     */
    public static TradePayoutSourceEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(TradePayoutSourceEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }
}
