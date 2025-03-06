package com.paysphere.command;


import com.paysphere.command.cmd.PaymentMethodStatusCommand;
import com.paysphere.command.cmd.PaymentMethodUpdateCommand;

public interface PaymentMethodCmdService {

    boolean updatePaymentMethod(PaymentMethodUpdateCommand command);

    boolean openOrClosePaymentMethod(PaymentMethodStatusCommand command);
}
