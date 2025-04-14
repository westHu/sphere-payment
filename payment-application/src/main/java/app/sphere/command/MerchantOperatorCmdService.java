package app.sphere.command;

import app.sphere.command.cmd.MerchantOperatorAddCmd;
import app.sphere.command.cmd.MerchantOperatorUpdateCmd;

public interface MerchantOperatorCmdService {

    boolean addMerchantOperator(MerchantOperatorAddCmd cmd);

    boolean updateMerchantOperator(MerchantOperatorUpdateCmd command);

}
