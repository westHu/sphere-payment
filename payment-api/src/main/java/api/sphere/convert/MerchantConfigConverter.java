package api.sphere.convert;


import app.sphere.command.cmd.MerchantConfigUpdateCmd;
import app.sphere.command.cmd.PaymentLinkSettingCmd;
import api.sphere.controller.request.MerchantConfigUpdateReq;
import api.sphere.controller.request.MerchantIdReq;
import api.sphere.controller.request.PaymentLinkSettingReq;
import app.sphere.query.param.MerchantIdParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantConfigConverter {

    MerchantIdParam convertMerchantIdParam(MerchantIdReq req);

    MerchantConfigUpdateCmd convertMerchantConfigUpdateCmd(MerchantConfigUpdateReq req);

    PaymentLinkSettingCmd convertPaymentLinkSettingCmd(PaymentLinkSettingReq req);
}
