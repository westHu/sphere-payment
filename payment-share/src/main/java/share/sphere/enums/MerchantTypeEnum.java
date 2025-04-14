package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 商户类型
 */
@Getter
@AllArgsConstructor
public enum MerchantTypeEnum {

    PERSON(1, "个人"),
    ENTERPRISE(2, "企业"),

    OTHER(99, "其他");

    private final Integer code;
    private final String name;


    public static String codeToName(Integer code) {
        if (Objects.isNull(code)) {
            return OTHER.getName();
        }
        return Arrays.stream(MerchantTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny().orElse(OTHER).getName();
    }


    public static Integer nameToCode(String name) {
        if (Objects.isNull(name)) {
            return OTHER.getCode();
        }
        return Arrays.stream(MerchantTypeEnum.values())
                .filter(e -> e.getName().equals(name))
                .findAny().orElse(OTHER).getCode();
    }

    public static MerchantTypeEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return OTHER;
        }
        return Arrays.stream(MerchantTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny().orElse(OTHER);
    }
}
