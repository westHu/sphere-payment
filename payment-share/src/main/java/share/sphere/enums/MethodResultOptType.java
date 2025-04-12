package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * redirect, display, share...
 */
@Getter
@AllArgsConstructor
public enum MethodResultOptType {

    DISPLAY("展示"),
    REDIRECT("跳转"),
    SHARE("分享"),
    IGNORE("忽略"),

    UNKNOWN("未知");

    private final String name;
}
