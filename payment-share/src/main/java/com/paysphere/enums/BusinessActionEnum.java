package com.paysphere.enums;

import com.paysphere.utils.BinaryUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum BusinessActionEnum {

    PAY_IN(1, "收款"),
    PAY_OUT(2, "出款");

    private final Integer code;
    private final String name;

    /**
     * codeToEnum
     */
    public static BusinessActionEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(BusinessActionEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(null);
    }

    /**
     * 转业务作用集合
     */
    public static List<BusinessActionEnum> businessActionEnumList(Integer action) {
        if (Objects.isNull(action)) {
            return new ArrayList<>();
        }

        List<Integer> cursor = BinaryUtil.find1Cursor(action);
        return cursor.stream()
                .map(BusinessActionEnum::codeToEnum)
                .filter(Objects::nonNull)
                .toList();
    }


}
