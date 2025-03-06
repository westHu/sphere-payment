package com.paysphere.command;

import com.paysphere.command.cmd.SandboxMerchantConfigUpdateCommand;

public interface SandboxMerchantConfigCmdService {

    Boolean updateSandboxMerchantConfig(SandboxMerchantConfigUpdateCommand command);
}
