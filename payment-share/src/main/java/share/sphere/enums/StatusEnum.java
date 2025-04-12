package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum StatusEnum {

    ENABLE(1, "有效"),
    DISABLE(2, "无效");

    private final Integer code;
    private final String name;

    /**
     * codeToEnum
     */
    public static StatusEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return ENABLE;
        }
        return Arrays.stream(StatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(ENABLE);
    }

}
