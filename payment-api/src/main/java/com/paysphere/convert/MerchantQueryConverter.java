package com.paysphere.convert;

import com.paysphere.controller.request.MerchantDropListReq;
import com.paysphere.controller.request.MerchantPageReq;
import com.paysphere.controller.response.MerchantBaseVO;
import com.paysphere.db.entity.Merchant;
import com.paysphere.query.param.MerchantDropListParam;
import com.paysphere.query.param.MerchantPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface MerchantQueryConverter {

    MerchantPageParam convertMerchantPageParam(MerchantPageReq req);

    List<MerchantBaseVO> convertMerchantBaseVOList(List<Merchant> data);

    MerchantBaseVO convertMerchantBaseVO(Merchant merchant);

    MerchantDropListParam convertMerchantDropListParam(MerchantDropListReq req);
}
