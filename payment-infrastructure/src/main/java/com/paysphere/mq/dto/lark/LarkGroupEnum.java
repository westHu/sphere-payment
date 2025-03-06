package com.paysphere.mq.dto.lark;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum LarkGroupEnum {

    GROUP_MONITOR("监控组群"),
    GROUP_EXCEPTION("异常组群"),
    GROUP_NOTIFY("通知组群"),
    GROUP_ALARM("报警组群"),

    GROUP_OTHER("其他组群");

    private final String desc;

    public static LarkGroupEnum codeToEnum(String name) {
        if (Objects.isNull(name)) {
            return GROUP_OTHER;
        }
        return Arrays.stream(LarkGroupEnum.values())
                .filter(e -> e.name().equals(name))
                .findAny().orElse(GROUP_OTHER);
    }

}