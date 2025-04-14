package app.sphere.command;

import app.sphere.command.cmd.SettleJobCommand;

public interface SettleJobCmdService {


    void fixD1TimeSettlePay(SettleJobCommand command);

    void fixD2TimeSettlePay(SettleJobCommand command);


    void fixT1TimeSettlePay(SettleJobCommand command);

    void fixT2TimeSettlePay(SettleJobCommand command);

}
