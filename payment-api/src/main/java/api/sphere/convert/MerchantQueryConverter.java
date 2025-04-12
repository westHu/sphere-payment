package api.sphere.convert;

import api.sphere.controller.request.MerchantDropListReq;
import api.sphere.controller.request.MerchantPageReq;
import api.sphere.controller.response.MerchantBaseVO;
import app.sphere.query.param.MerchantDropListParam;
import infrastructure.sphere.db.entity.Merchant;
import app.sphere.query.param.MerchantPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface MerchantQueryConverter {

    MerchantPageParam convertMerchantPageParam(MerchantPageReq req);

    List<MerchantBaseVO> convertMerchantBaseVOList(List<Merchant> data);

    MerchantBaseVO convertMerchantBaseVO(Merchant merchant);

    MerchantDropListParam convertMerchantDropListParam(MerchantDropListReq req);
}
