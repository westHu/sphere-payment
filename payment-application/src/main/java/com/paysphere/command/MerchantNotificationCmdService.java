package com.paysphere.command;

import com.paysphere.command.cmd.MerchantNotificationAddCommand;

public interface MerchantNotificationCmdService {

    boolean addMerchantNotification(MerchantNotificationAddCommand command);

}
