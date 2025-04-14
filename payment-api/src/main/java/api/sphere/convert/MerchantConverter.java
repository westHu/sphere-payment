package api.sphere.convert;

import api.sphere.controller.request.*;
import api.sphere.controller.response.MerchantBaseVO;
import app.sphere.command.cmd.*;
import app.sphere.query.param.*;
import infrastructure.sphere.db.entity.Merchant;
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
