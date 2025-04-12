package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum QuerySourceEnum {

    MERCHANT_ADMIN(1), ADMIN(2);

    private final Integer code;

    /**
     * codeToEnum
     */
    public static QuerySourceEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return MERCHANT_ADMIN;
        }
        return Arrays.stream(QuerySourceEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(MERCHANT_ADMIN);
    }
}
