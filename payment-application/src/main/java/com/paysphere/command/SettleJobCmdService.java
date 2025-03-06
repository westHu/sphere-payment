package com.paysphere.command;

import com.paysphere.command.cmd.SettleJobCommand;

public interface SettleJobCmdService {

    void fixD0TimeSettlePay(SettleJobCommand command);

    void fixD1TimeSettlePay(SettleJobCommand command);

    void fixD2TimeSettlePay(SettleJobCommand command);


    void fixT1TimeSettlePay(SettleJobCommand command);

    void fixT2TimeSettlePay(SettleJobCommand command);




    void fixT0TimeSettlePay(SettleJobCommand command);

    void fixManualTimeSettlePay(SettleJobCommand command);

}
