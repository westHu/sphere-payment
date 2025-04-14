package api.sphere.convert;

import api.sphere.controller.request.MerchantWithdrawConfigUpdateReq;
import api.sphere.controller.request.MerchantWithdrawReq;
import app.sphere.command.cmd.MerchantWithdrawCommand;
import app.sphere.command.cmd.MerchantWithdrawConfigUpdateCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantWithdrawConverter {

    MerchantWithdrawCommand convertMerchantWithdrawCommand(MerchantWithdrawReq req);

    MerchantWithdrawConfigUpdateCommand convertMerchantWithdrawConfigUpdateCommand(MerchantWithdrawConfigUpdateReq req);
}
