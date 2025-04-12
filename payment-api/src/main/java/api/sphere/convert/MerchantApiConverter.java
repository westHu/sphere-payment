package api.sphere.convert;


import app.sphere.command.cmd.MerchantUpdateStatusCommand;
import api.sphere.controller.request.MerchantUpdateStatusReq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantApiConverter {

    MerchantUpdateStatusCommand convertMerchantUpdateStatusCommand(MerchantUpdateStatusReq req);
}
