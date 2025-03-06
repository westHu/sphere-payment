package com.paysphere.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum PaymentMethodEnum {

    MANDIRI("mandiri"),
    BNC("bnc"),
    BCA("bca"),
    BNI( "bni"),
    W_OVO( "W_OVO"),
    USDT( "usdt"),
    BTC( "BTC"),
    BNB( "BNB"),
    ETH( "ETH"),
    TRX( "TRX"),
    USDC( "USDC"),

    ;


    private final String name;

    public static boolean checkMandiri(String name){
        return  StringUtils.equalsIgnoreCase(name,MANDIRI.getName());
    }
}
