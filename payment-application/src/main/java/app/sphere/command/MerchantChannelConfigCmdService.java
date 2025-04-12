package app.sphere.command;

import app.sphere.command.cmd.MerchantChannelConfigUpdateCmd;

public interface MerchantChannelConfigCmdService {

    boolean updateMerchantChannel(MerchantChannelConfigUpdateCmd cmd);

}
