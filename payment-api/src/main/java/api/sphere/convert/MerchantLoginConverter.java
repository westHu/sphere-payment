package api.sphere.convert;

import api.sphere.controller.request.*;
import app.sphere.command.cmd.*;
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
}
