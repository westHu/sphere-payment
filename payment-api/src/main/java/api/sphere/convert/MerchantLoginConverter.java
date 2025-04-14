package api.sphere.convert;

import api.sphere.controller.request.UnsetGoogleCodeReq;
import app.sphere.command.cmd.MerchantLoginCmd;
import app.sphere.command.cmd.MerchantPasswordChannelCmd;
import app.sphere.command.cmd.MerchantPasswordForgetCmd;
import app.sphere.command.cmd.MerchantPasswordResetCmd;
import app.sphere.command.cmd.MerchantSetGoogleCodeCmd;
import app.sphere.command.cmd.MerchantShowGoogleCodeCmd;
import app.sphere.command.cmd.MerchantUnsetGoogleCodeCmd;
import app.sphere.command.cmd.MerchantVerifyGoogleCodeCmd;
import api.sphere.controller.request.MerchantLoginReq;
import api.sphere.controller.request.MerchantPasswordChangeReq;
import api.sphere.controller.request.MerchantPasswordForgetReq;
import api.sphere.controller.request.MerchantPasswordResetReq;
import api.sphere.controller.request.MerchantSetGoogleCodeReq;
import api.sphere.controller.request.MerchantShowGoogleCodeReq;
import api.sphere.controller.request.MerchantVerifyGoogleCodeReq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantLoginConverter {

    MerchantLoginCmd convertMerchantLoginCmd(MerchantLoginReq req);

    MerchantPasswordForgetCmd convertMerchantPasswordForgetCmd(MerchantPasswordForgetReq req);

    MerchantVerifyGoogleCodeCmd convertMerchantVerifyGoogleCodeCommand(MerchantVerifyGoogleCodeReq req);

    MerchantShowGoogleCodeCmd convertMerchantShowGoogleCodeCmd(MerchantShowGoogleCodeReq req);

    MerchantSetGoogleCodeCmd convertMerchantSetGoogleCodeCmd(MerchantSetGoogleCodeReq req);

    MerchantPasswordResetCmd convertMerchantPasswordResetCmd(MerchantPasswordResetReq req);

    MerchantUnsetGoogleCodeCmd convertMerchantUnsetGoogleCodeCmd(UnsetGoogleCodeReq req);

    MerchantPasswordChannelCmd convertMerchantPasswordChannelCmd(MerchantPasswordChangeReq req);
}
