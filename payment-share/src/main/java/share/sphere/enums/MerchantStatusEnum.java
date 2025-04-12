package share.sphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 商户状态
 * 未激活/正常/冻结/休眠/注销...
 */
@Getter
@AllArgsConstructor
public enum MerchantStatusEnum {

    PENDING(0, "待激活"),
    NORMAL(1, "正常"),
    CANCEL(-1, "注销"),
    FROZEN(-2, "冻结"),
    DORMANT(-3, "休眠");

    private final Integer code;
    private final String name;

    public static MerchantStatusEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return NORMAL;
        }
        return Arrays.stream(MerchantStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny().orElse(NORMAL);
    }
}
