package com.paysphere.mq.dto.lark;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum LarkMessageTypeEnum {

    TEXT("文本消息"),
    TEMPLATE("模版消息"),
    APP_INQUIRY("查单机器人应用服务"),

    OTHER("其他组群");

    private final String desc;

    public static LarkMessageTypeEnum codeToEnum(String name) {
        if (Objects.isNull(name)) {
            return OTHER;
        }
        return Arrays.stream(LarkMessageTypeEnum.values())
                .filter(e -> e.name().equals(name))
                .findAny().orElse(OTHER);
    }

}