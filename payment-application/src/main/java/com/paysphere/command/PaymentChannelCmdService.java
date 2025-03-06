package com.paysphere.command;

import com.paysphere.command.cmd.PaymentChannelStatusCommand;
import com.paysphere.command.cmd.PaymentChannelUpdateCommand;

public interface PaymentChannelCmdService {

    boolean updatePaymentChannel(PaymentChannelUpdateCommand command);

    boolean openOrClosePaymentChannel(PaymentChannelStatusCommand command);
}
