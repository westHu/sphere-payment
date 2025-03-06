package com.paysphere.command;

import com.paysphere.command.cmd.SettleRefundCmd;
import com.paysphere.command.cmd.SettleSupplementCmd;

public interface SettleRefundCmdService {

    boolean handlerSupplement(SettleSupplementCmd command);

    boolean handlerRefund(SettleRefundCmd command);
}
