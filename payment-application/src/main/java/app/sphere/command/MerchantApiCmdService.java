package app.sphere.command;


import app.sphere.command.cmd.MerchantUpdateStatusCommand;

public interface MerchantApiCmdService {

    boolean updateMerchantStatus(MerchantUpdateStatusCommand updateStatusCommand);

}
