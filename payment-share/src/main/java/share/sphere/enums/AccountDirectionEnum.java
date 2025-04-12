package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AccountDirectionEnum {


    INCOME(1, "收入"),
    EXPEND(-1, "支出"),
    ;

    private final Integer code;
    private final String name;

    public static String codeToName(Integer code) {
        if (Objects.isNull(code)) {
            return INCOME.name();
        }
        return Arrays.stream(AccountDirectionEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .map(Enum::name)
                .orElse(INCOME.name());
    }
}
