package app.sphere.command;


import app.sphere.command.cmd.MerchantAddCommand;
import app.sphere.command.cmd.MerchantUpdateCommand;
import app.sphere.command.cmd.MerchantVerifyCommand;

public interface MerchantCmdService {

    boolean addMerchant(MerchantAddCommand command);

    boolean verifyMerchant(MerchantVerifyCommand command);

    boolean updateMerchant(MerchantUpdateCommand updateStatusCommand);
}
