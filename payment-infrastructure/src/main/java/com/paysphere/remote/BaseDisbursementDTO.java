package com.paysphere.remote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class BaseDisbursementDTO {

    /**
     * 渠道返回的流水号
     */
    private String channelOrderNo;

    /**
     * 是否需要callback来判断终态
     */
    @JsonIgnore
    private boolean callBack = true;


    public boolean getCallBack() {
        return callBack;
    }
}
