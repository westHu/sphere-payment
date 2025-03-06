package com.paysphere.command;

import com.paysphere.command.cmd.MerchantChannelConfigUpdateCmd;
import com.paysphere.command.cmd.MerchantIdCommand;

public interface MerchantChannelConfigCmdService {

    boolean updateMerchantChannelStatus(MerchantChannelConfigUpdateCmd cmd);

    boolean updateMerchantChannelPriority(MerchantChannelConfigUpdateCmd cmd);

    boolean updateMerchantChannelFee(MerchantChannelConfigUpdateCmd cmd);

    boolean syncMerchantChannelConfig(MerchantIdCommand cmd);

}
