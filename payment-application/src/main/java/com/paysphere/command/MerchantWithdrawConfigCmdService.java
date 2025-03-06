package com.paysphere.command;


import com.paysphere.command.cmd.MerchantWithdrawCommand;
import com.paysphere.command.cmd.MerchantWithdrawConfigUpdateCommand;

public interface MerchantWithdrawConfigCmdService {

    boolean updateMerchantWithdrawConfig(MerchantWithdrawConfigUpdateCommand command);

    boolean withdraw(MerchantWithdrawCommand command);
}
