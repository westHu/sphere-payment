package api.sphere.convert;

import app.sphere.command.cmd.MerchantChannelConfigUpdateCmd;
import api.sphere.controller.request.MerchantChannelConfigListReq;
import api.sphere.controller.request.MerchantChannelConfigUpdateReq;
import app.sphere.query.param.MerchantChannelConfigListParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantChannelConfigConverter {

    MerchantChannelConfigListParam convertMerchantChannelConfigListParam(MerchantChannelConfigListReq req);

    MerchantChannelConfigUpdateCmd convertMerchantChannelConfigUpdateCmd(MerchantChannelConfigUpdateReq req);

}
