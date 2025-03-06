package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum PaymentTypeEnum {

    VIRTUAL_ACCOUNT(1, "虚拟号支付"),
    QRIS(2, "QR码支付"),
    CREDIT_CARD(3, "信用卡支付"),
    E_WALLET(4, "钱包支付"),
    ONLINE_BANKING(5, "在线银行业务"),
    RETAIL_STORE(6, "便利店"),
    DIGITAL(7, "数字货币"),
    CTC(8, "CardToCard"),

    UNKNOWN(99, "未知"),
    ;

    private final Integer code;
    private final String name;

    /**
     * 根据code查询状态
     *
     * @param code
     * @return
     */
    public static String codeToName(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN.getName();
        }
        return Arrays.stream(PaymentTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN)
                .getName();
    }

    /**
     * 根据code查询状态
     *
     * @param name
     * @return
     */
    public static Integer nameToCode(String name) {
        if (StringUtils.isBlank(name)) {
            return UNKNOWN.getCode();
        }
        return Arrays.stream(PaymentTypeEnum.values())
                .filter(e -> e.getName().equals(name))
                .map(PaymentTypeEnum::getCode)
                .findAny().orElse(UNKNOWN.getCode());
    }


    /**
     * @param code
     * @return
     */
    public static PaymentTypeEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(PaymentTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny().orElse(UNKNOWN);
    }


}
