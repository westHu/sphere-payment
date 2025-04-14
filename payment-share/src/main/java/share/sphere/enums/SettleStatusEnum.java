package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 结算状态
 */
@Getter
@AllArgsConstructor
public enum SettleStatusEnum {

    SETTLE_TODO(0, "未结算"),
    SETTLE_PROCESSING(2, "结算中"),
    SETTLE_SUCCESS(1, "结算成功"),
    SETTLE_FAILED(-1, "结算失败"),
    SETTLE_CANCEL(-1, "结算失败"),

    UNKNOWN(99, "未知");


    private final Integer code;
    private final String name;

    /**
     * 需要结算的状态
     */
    public static List<Integer> needToSettle() {
        return Stream.of(SETTLE_TODO, SETTLE_FAILED).map(SettleStatusEnum::getCode).collect(Collectors.toList());
    }

    public static SettleStatusEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(SettleStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }


    /**
     * codeToName
     */
    public static String codeToName(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN.getName();
        }
        return Arrays.stream(SettleStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN)
                .getName();
    }

    /**
     * nameToCode
     */
    public static Integer nameToCode(String name) {
        if (StringUtils.isBlank(name)) {
            return UNKNOWN.getCode();
        }
        return Arrays.stream(SettleStatusEnum.values())
                .filter(e -> e.getName().equals(name))
                .findAny()
                .orElse(UNKNOWN)
                .getCode();
    }

}
