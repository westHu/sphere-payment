package com.paysphere.command;



import com.paysphere.command.cmd.PaymentChannelMethodAddCommand;
import com.paysphere.command.cmd.PaymentChannelMethodStatusCommand;
import com.paysphere.command.cmd.PaymentChannelMethodUpdateCommand;

import java.util.List;

public interface PaymentChannelMethodCmdService {

    boolean openOrClosePaymentChannelMethod(List<PaymentChannelMethodStatusCommand> commandList);

    boolean addPaymentChannelMethod(PaymentChannelMethodAddCommand command);

    boolean updatePaymentChannelMethod(PaymentChannelMethodUpdateCommand command);

    boolean deletePaymentChannelMethod(Long id);
}
