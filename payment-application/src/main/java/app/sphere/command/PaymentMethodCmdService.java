package app.sphere.command;


import app.sphere.command.cmd.PaymentMethodStatusCommand;
import app.sphere.command.cmd.PaymentMethodUpdateCommand;

public interface PaymentMethodCmdService {

    boolean updatePaymentMethod(PaymentMethodUpdateCommand command);

    boolean openOrClosePaymentMethod(PaymentMethodStatusCommand command);
}
