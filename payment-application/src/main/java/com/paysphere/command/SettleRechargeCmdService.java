package com.paysphere.command;

import com.paysphere.command.cmd.SettleRechargeCommand;

public interface SettleRechargeCmdService {

    void handlerRecharge(SettleRechargeCommand command);

}
