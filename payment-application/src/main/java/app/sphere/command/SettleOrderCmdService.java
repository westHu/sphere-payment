package app.sphere.command;

import app.sphere.command.cmd.SettleRefundCmd;
import app.sphere.command.cmd.SettleSupplementCmd;

public interface SettleOrderCmdService {

    boolean supplement(SettleSupplementCmd command);

    boolean refund(SettleRefundCmd command);
}
