package com.paysphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商户等级
 */
@Getter
@AllArgsConstructor
public enum MerchantLevelEnum {

    BASIC_MEMBER(0),
    SILVER_MEMBER(1),
    GOLD_MEMBER(2),
    PLATINUM_MEMBER(3),
    DIAMOND_MEMBER(4),
    OTHER(99);

    private final Integer code;


}
