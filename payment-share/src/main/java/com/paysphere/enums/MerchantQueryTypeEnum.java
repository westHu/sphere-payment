package com.paysphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MerchantQueryTypeEnum {

    BASE, BASE_EXT, CONFIG,  //基本

    PAYMENT_CONFIG, PAYMENT_CHANNEL_CONFIG, //收款

    PAYOUT_CONFIG, PAYOUT_CHANNEL_CONFIG,  //代付

    WITHDRAW_CONFIG, WITHDRAW_CHANNEL_CONFIG, //提现

    ACCOUNT, OPERATOR,  //账户，操作员

    AGENT_PAY_CONFIG, AGENT_CASH_CONFIG //代理商

}
