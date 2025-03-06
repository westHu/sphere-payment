package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentWalletEnum {

    SHOPEEPAY("SHOPEEPAY"),  OVO("OVO"),  DANA( "DANA");


    private final String name;

}
