package com.paysphere.convert;

import com.paysphere.command.cmd.MerchantChannelConfigUpdateCmd;
import com.paysphere.controller.request.MerchantChannelConfigListReq;
import com.paysphere.controller.request.MerchantChannelConfigUpdateReq;
import com.paysphere.query.param.MerchantChannelConfigListParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantChannelConfigConverter {

    MerchantChannelConfigListParam convertMerchantChannelConfigListParam(MerchantChannelConfigListReq req);

    MerchantChannelConfigUpdateCmd convertMerchantChannelConfigUpdateCmd(MerchantChannelConfigUpdateReq req);

    MerchantChannelConfigUpdateCmd convertMerchantChannelConfigUpdateCommand(MerchantChannelConfigUpdateReq req);

}
