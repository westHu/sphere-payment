package com.paysphere.remote;

import lombok.Data;

/**
 * channel result
 */
@Data
public class ChannelResult<T> {

    /**
     * success
     */
    private boolean isSuccess;

    /**
     * msg
     */
    private String errorMsg;

    /**
     * bean
     */
    private T payload;


    public ChannelResult(String errorMsg) {
        this.isSuccess = false;
        this.errorMsg = errorMsg;
        this.payload = null;
    }

    public ChannelResult(T payload) {
        this.isSuccess = true;
        this.errorMsg = null;
        this.payload = payload;
    }

    public ChannelResult(boolean isSuccess, T payload) {
        this.isSuccess = isSuccess;
        this.errorMsg = null;
        this.payload = payload;
    }
}
