package app.sphere.command;

import app.sphere.command.cmd.SandboxMerchantConfigUpdateCommand;

public interface SandboxMerchantConfigCmdService {

    Boolean updateSandboxMerchantConfig(SandboxMerchantConfigUpdateCommand command);
}
