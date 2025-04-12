package app.sphere.command;

import app.sphere.command.cmd.SettlePayoutCommand;
import infrastructure.sphere.db.entity.SettleOrder;

public interface SettlePayoutCmdService {

    void handlerSettleImmediate(SettlePayoutCommand command);

}
