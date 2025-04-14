package app.sphere.command;

import app.sphere.command.cmd.SettlePaymentCommand;
import share.sphere.enums.SettleStatusEnum;

public interface SettlePaymentCmdService {

    void handlerSettleImmediate(SettlePaymentCommand command);

    void handlerSettleJob(SettlePaymentCommand command);

    void addSettleOrder(SettlePaymentCommand messageCommand, SettleStatusEnum settleStatusEnum);

}
