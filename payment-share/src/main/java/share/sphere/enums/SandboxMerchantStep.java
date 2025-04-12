package share.sphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 商户对接步骤
 */

@Getter
@AllArgsConstructor
public enum SandboxMerchantStep {

    INFORMATION(1, "获取对接信息"),
    SETTING(2, "填写API设置"),
    ORDER(3, "根据对接文档生成订单"),
    NOTIFICATION(4, "验证订单通知信息"),
    UNKNOWN(-1, "未知步骤");

    private final Integer code;
    private final String name;

    public static SandboxMerchantStep nameToEnum(String name) {
        if (StringUtils.isBlank(name)) {
            return UNKNOWN;
        }
        return Arrays.stream(SandboxMerchantStep.values())
                .filter(e -> e.name().equals(name))
                .findAny().orElse(UNKNOWN);
    }


}
