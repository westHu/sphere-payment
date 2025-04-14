package app.sphere.command.dto;

import lombok.Data;

@Data
public class AccountRecordAttributeDTO {


    /**
     * 转出账户信息
     */
    private TransferOutAccountDTO transferOutAccount;

    /**
     * 转入账户信息
     */
    private TransferToAccountDTO transferToAccount;

}
