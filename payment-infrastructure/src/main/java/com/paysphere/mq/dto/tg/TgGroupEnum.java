package com.paysphere.mq.dto.tg;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TgGroupEnum {

    GROUP_EXCEPTION("异常组群"),
    GROUP_CHANNEL("渠道组群"),
    GROUP_RECONCILE("对账组群"),
    GROUP_BUSINESS("业务组群"),
    GROUP_ALARM("警告组群"),

    GROUP_OTHER("其他组群");

    private final String desc;

    public static TgGroupEnum codeToEnum(String name) {
        if (Objects.isNull(name)) {
            return GROUP_OTHER;
        }
        return Arrays.stream(TgGroupEnum.values())
                .filter(e -> e.name().equals(name))
                .findAny().orElse(GROUP_OTHER);
    }

}