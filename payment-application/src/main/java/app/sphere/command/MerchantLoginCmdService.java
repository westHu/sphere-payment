package app.sphere.command;

import app.sphere.command.cmd.MerchantPasswordChannelCmd;
import app.sphere.command.cmd.MerchantPasswordForgetCmd;
import app.sphere.command.cmd.MerchantSetGoogleCodeCmd;
import app.sphere.command.cmd.MerchantShowGoogleCodeCmd;
import app.sphere.command.cmd.MerchantUnsetGoogleCodeCmd;
import app.sphere.command.cmd.MerchantVerifyGoogleCodeCmd;
import app.sphere.command.dto.MerchantLoginDTO;
import app.sphere.command.cmd.MerchantLoginCmd;
import app.sphere.command.cmd.MerchantPasswordResetCmd;

public interface MerchantLoginCmdService {

    MerchantLoginDTO merchantLogin(MerchantLoginCmd command);

    boolean verifyGoogleCode(MerchantVerifyGoogleCodeCmd command);

    boolean forgetPassword(MerchantPasswordForgetCmd command);

    boolean changePassword(MerchantPasswordChannelCmd command);

    String showGoogleAuth(MerchantShowGoogleCodeCmd command);

    boolean setGoogleCode(MerchantSetGoogleCodeCmd command);

    boolean resetPassword(MerchantPasswordResetCmd command);

    boolean unsetGoogleAuth(MerchantUnsetGoogleCodeCmd command);
}
