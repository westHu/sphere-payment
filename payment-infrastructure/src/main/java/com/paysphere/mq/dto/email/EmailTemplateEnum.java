package com.paysphere.mq.dto.email;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplateEnum {

    TRADE_TRANSFER, TRADE_WITHDRAW, TRADE_PAYIN_RECEIPT, TRADE_PAYOUT_RECEIPT,
    MERCHANT_SEND_CODE

}
