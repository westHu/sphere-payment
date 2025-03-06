package com.paysphere.remote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Getter
@AllArgsConstructor
public enum ChannelEnum {

    C_001("C_001", "C_001"),
    C_MOCK("C_MOCK", "MOCK"),
    ;

    private final String code;
    private final String name;


    public static ChannelEnum codeToEnum(String code) {
        if (StringUtils.isBlank(code)) {
            return C_MOCK;
        }

        return Arrays.stream(ChannelEnum.values())
                .filter(e -> Objects.equals(e.getCode(), code))
                .findAny()
                .orElse(C_MOCK);
    }

}
