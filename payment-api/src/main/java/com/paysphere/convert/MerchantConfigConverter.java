package com.paysphere.convert;


import com.paysphere.command.cmd.MerchantConfigUpdateCmd;
import com.paysphere.command.cmd.PaymentLinkSettingCmd;
import com.paysphere.controller.request.MerchantConfigUpdateReq;
import com.paysphere.controller.request.MerchantIdReq;
import com.paysphere.controller.request.PaymentLinkSettingReq;
import com.paysphere.query.param.MerchantIdParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantConfigConverter {

    MerchantIdParam convertMerchantIdParam(MerchantIdReq req);

    MerchantConfigUpdateCmd convertMerchantConfigUpdateCmd(MerchantConfigUpdateReq req);

    PaymentLinkSettingCmd convertPaymentLinkSettingCmd(PaymentLinkSettingReq req);
}
