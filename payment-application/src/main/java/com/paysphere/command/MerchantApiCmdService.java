package com.paysphere.command;


import com.paysphere.command.cmd.MerchantUpdateStatusCommand;

public interface MerchantApiCmdService {

    boolean sendCode2Email(String email);

    boolean verifyCode4Email(String email, String code);

    boolean updateMerchantStatus(MerchantUpdateStatusCommand updateStatusCommand);

}
