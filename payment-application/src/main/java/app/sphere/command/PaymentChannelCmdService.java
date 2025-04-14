package app.sphere.command;

import app.sphere.command.cmd.PaymentChannelStatusCommand;
import app.sphere.command.cmd.PaymentChannelUpdateCommand;

public interface PaymentChannelCmdService {

    boolean updatePaymentChannel(PaymentChannelUpdateCommand command);

    boolean openOrClosePaymentChannel(PaymentChannelStatusCommand command);
}
