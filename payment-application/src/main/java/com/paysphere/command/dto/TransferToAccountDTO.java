package com.paysphere.command.dto;

import lombok.Data;

@Data
public class TransferToAccountDTO {

    /**
     * 转入商户ID
     */
    private String transferToMerchantId;

    /**
     * 转入商户名称
     */
    private String transferToMerchantName;

    /**
     * 转入账户
     */
    private String transferToAccount;
}
