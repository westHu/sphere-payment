package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ApiModeEnum {

    API(1, "API模式"),
    CASHIER(2, "收银台模式")
    ;

    private final Integer code;
    private final String name;


    /**
     * codeToEnum
     */
    public static ApiModeEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return API;
        }
        return Arrays.stream(ApiModeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(API);
    }

}
