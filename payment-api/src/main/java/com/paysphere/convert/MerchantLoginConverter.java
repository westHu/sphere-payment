package com.paysphere.convert;

import com.paysphere.command.cmd.MerchantLoginCmd;
import com.paysphere.command.cmd.MerchantPasswordChannelCmd;
import com.paysphere.command.cmd.MerchantPasswordForgetCmd;
import com.paysphere.command.cmd.MerchantPasswordResetCmd;
import com.paysphere.command.cmd.MerchantSetGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantShowGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantUnsetGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantVerifyGoogleCodeCmd;
import com.paysphere.command.dto.LoginDTO;
import com.paysphere.controller.request.MerchantLoginReq;
import com.paysphere.controller.request.MerchantPasswordChangeReq;
import com.paysphere.controller.request.MerchantPasswordForgetReq;
import com.paysphere.controller.request.MerchantPasswordResetReq;
import com.paysphere.controller.request.MerchantSetGoogleCodeReq;
import com.paysphere.controller.request.MerchantShowGoogleCodeReq;
import com.paysphere.controller.request.MerchantUnsetGoogleCodeReq;
import com.paysphere.controller.request.MerchantVerifyGoogleCodeReq;
import com.paysphere.controller.response.LoginVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantLoginConverter {

    MerchantLoginCmd convertMerchantLoginCmd(MerchantLoginReq req);

    MerchantPasswordForgetCmd convertMerchantPasswordForgetCmd(MerchantPasswordForgetReq req);

    MerchantVerifyGoogleCodeCmd convertMerchantVerifyGoogleCodeCommand(MerchantVerifyGoogleCodeReq req);

    MerchantShowGoogleCodeCmd convertMerchantShowGoogleCodeCmd(MerchantShowGoogleCodeReq req);

    MerchantSetGoogleCodeCmd convertMerchantSetGoogleCodeCmd(MerchantSetGoogleCodeReq req);

    MerchantPasswordResetCmd convertMerchantPasswordResetCmd(MerchantPasswordResetReq req);

    MerchantUnsetGoogleCodeCmd convertMerchantUnsetGoogleCodeCmd(MerchantUnsetGoogleCodeReq req);

    MerchantPasswordChannelCmd convertMerchantPasswordChannelCmd(MerchantPasswordChangeReq req);

    LoginVO convertLoginVO(LoginDTO dto);
}
