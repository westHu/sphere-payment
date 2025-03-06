package com.paysphere.command;

import com.paysphere.command.cmd.SettleCashMessageCommand;
import com.paysphere.db.entity.SettleOrder;

public interface SettleCashCmdService {

    void handlerSettleImmediate(SettleCashMessageCommand command);

    void handlerReSettle(SettleOrder order, String operator);
}
