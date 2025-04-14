package app.sphere.command;


import app.sphere.command.cmd.PaymentChannelMethodAddCommand;
import app.sphere.command.cmd.PaymentChannelMethodStatusCommand;
import app.sphere.command.cmd.PaymentChannelMethodUpdateCommand;

public interface PaymentChannelMethodCmdService {

    boolean openOrClosePaymentChannelMethod(PaymentChannelMethodStatusCommand commandList);

    boolean addPaymentChannelMethod(PaymentChannelMethodAddCommand command);

    boolean updatePaymentChannelMethod(PaymentChannelMethodUpdateCommand command);
}
