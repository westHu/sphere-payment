package api.sphere.convert;

import api.sphere.controller.request.MerchantAddReq;
import api.sphere.controller.request.MerchantDropListReq;
import api.sphere.controller.request.MerchantIdReq;
import api.sphere.controller.request.MerchantPageReq;
import api.sphere.controller.request.MerchantUpdateReq;
import api.sphere.controller.request.MerchantVerifyReq;
import api.sphere.controller.response.MerchantBaseVO;
import app.sphere.command.cmd.MerchantAddCommand;
import app.sphere.command.cmd.MerchantUpdateCommand;
import app.sphere.command.cmd.MerchantVerifyCommand;
import app.sphere.query.param.MerchantDropListParam;
import app.sphere.query.param.MerchantIdParam;
import infrastructure.sphere.db.entity.Merchant;
import app.sphere.query.param.MerchantPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface MerchantConverter {

    MerchantPageParam convertMerchantPageParam(MerchantPageReq req);

    List<MerchantBaseVO> convertMerchantBaseVOList(List<Merchant> data);

    MerchantBaseVO convertMerchantBaseVO(Merchant merchant);

    MerchantDropListParam convertMerchantDropListParam(MerchantDropListReq req);

    MerchantAddCommand convertMerchantAddCommand(MerchantAddReq req);

    MerchantUpdateCommand convertMerchantUpdateStatusCommand(MerchantUpdateReq req);

    MerchantVerifyCommand convertMerchantVerifyCommand(MerchantVerifyReq req);

    MerchantIdParam convertMerchantIdParam(MerchantIdReq req);
}
