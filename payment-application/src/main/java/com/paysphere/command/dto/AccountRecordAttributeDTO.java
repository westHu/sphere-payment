package com.paysphere.command.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccountRecordAttributeDTO {

    /**
     * 商户分润明细
     */
    private List<MerchantProfitDTO> merchantProfitList;

    /**
     * 转出账户信息
     */
    private TransferOutAccountDTO transferOutAccount;

    /**
     * 转入账户信息
     */
    private TransferToAccountDTO transferToAccount;

}
