package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AccountTypeEnum {

    PLATFORM_ACC(0, "平台账户"),
    MERCHANT_ACC(1, "商户账户");

    private final Integer code;
    private final String name;


    public static AccountTypeEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return MERCHANT_ACC;
        }
        return Arrays.stream(AccountTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(MERCHANT_ACC);
    }


}
