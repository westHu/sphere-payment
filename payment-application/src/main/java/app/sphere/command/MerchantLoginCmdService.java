package app.sphere.command;

import app.sphere.command.cmd.*;
import app.sphere.command.dto.MerchantLoginDTO;

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
