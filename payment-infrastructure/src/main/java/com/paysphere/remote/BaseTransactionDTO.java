package com.paysphere.remote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class BaseTransactionDTO {

    /**
     * 渠道返回的流水号
     */
    private String channelOrderNo;

    /**
     * 渠道返回的二维码
     */
    private String channelQr;

    /**
     * 渠道返回的paymentUrl链接
     */
    private String channelPaymentUrl;

    /**
     * 是否需要callback来判断终态
     */
    @JsonIgnore
    private boolean callBack = true;

}
