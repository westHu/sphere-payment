package app.sphere.command;


import app.sphere.command.cmd.*;

public interface MerchantCmdService {

    boolean addMerchant(MerchantAddCommand command);

    boolean verifyMerchant(MerchantVerifyCommand command);

    boolean updateMerchant(MerchantUpdateCommand updateStatusCommand);
}
