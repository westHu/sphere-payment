package com.paysphere.command.cmd;

import lombok.Data;

/**
 * 商户注册
 */
@Data
public class MerchantUpdateStatusCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户状态
     */
    private Integer status;
}
