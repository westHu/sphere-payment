package com.paysphere.command;

import com.paysphere.command.cmd.MerchantLoginCmd;
import com.paysphere.command.cmd.MerchantPasswordChannelCmd;
import com.paysphere.command.cmd.MerchantPasswordForgetCmd;
import com.paysphere.command.cmd.MerchantPasswordResetCmd;
import com.paysphere.command.cmd.MerchantSetGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantShowGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantUnsetGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantVerifyGoogleCodeCmd;
import com.paysphere.command.dto.LoginDTO;

public interface MerchantLoginCmdService {

    LoginDTO merchantLogin(MerchantLoginCmd command);

    boolean verifyGoogleCode(MerchantVerifyGoogleCodeCmd command);

    boolean forgetPassword(MerchantPasswordForgetCmd command);

    boolean changePassword(MerchantPasswordChannelCmd command);

    String showGoogleAuth(MerchantShowGoogleCodeCmd command);

    boolean setGoogleCode(MerchantSetGoogleCodeCmd command);

    boolean resetPassword(MerchantPasswordResetCmd command);

    boolean unsetGoogleAuth(MerchantUnsetGoogleCodeCmd command);
}
