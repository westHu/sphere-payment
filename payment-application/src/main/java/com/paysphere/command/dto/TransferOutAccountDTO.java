package com.paysphere.command.dto;

import lombok.Data;

@Data
public class TransferOutAccountDTO {

    /**
     * 转出商户ID
     */
    private String transferOutMerchantId;

    /**
     * 转出商户名称
     */
    private String transferOutMerchantName;

    /**
     * 转出账户
     */
    private String transferOutAccount;
}
