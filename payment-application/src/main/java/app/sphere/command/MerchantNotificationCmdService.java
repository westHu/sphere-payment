package app.sphere.command;

import app.sphere.command.cmd.MerchantNotificationAddCommand;

public interface MerchantNotificationCmdService {

    boolean addMerchantNotification(MerchantNotificationAddCommand command);

}
