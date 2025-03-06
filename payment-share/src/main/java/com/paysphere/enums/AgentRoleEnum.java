package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgentRoleEnum {

    BD("Business Development"), //BD肯定是FA

    FA("First-level Agent"), //FA不一定是BD

    SA("Secondary Agent");

    private final String name;


    //       smile
    //        |
    //    BD     FA
    //    |      |
    //  SA SA  SA SA


}
