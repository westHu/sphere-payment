package com.paysphere.command;

import com.paysphere.command.cmd.SettlePayMessageCommand;
import com.paysphere.enums.SettleStatusEnum;

public interface SettlePayCmdService {

    void handlerSettleImmediate(SettlePayMessageCommand command);

    void handlerSettleJob(SettlePayMessageCommand command);

    void addSettleOrder(SettlePayMessageCommand messageCommand, SettleStatusEnum settleStatusEnum);

}
