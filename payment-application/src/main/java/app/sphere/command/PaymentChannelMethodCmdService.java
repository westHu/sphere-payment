package app.sphere.command;


import app.sphere.command.cmd.*;

public interface PaymentChannelMethodCmdService {

    boolean openOrClosePaymentChannelMethod(PaymentChannelMethodStatusCommand commandList);

    boolean addPaymentChannelMethod(PaymentChannelMethodAddCommand command);

    boolean updatePaymentChannelMethod(PaymentChannelMethodUpdateCommand command);
}
