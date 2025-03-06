package com.paysphere.command;

import com.paysphere.command.cmd.MerchantOperatorAddCmd;
import com.paysphere.command.cmd.MerchantOperatorUpdateCmd;

public interface MerchantOperatorCmdService {

    boolean addMerchantOperator(MerchantOperatorAddCmd cmd);

    boolean updateMerchantOperator(MerchantOperatorUpdateCmd command);

}
