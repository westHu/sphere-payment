package com.paysphere.convert;

import com.paysphere.controller.request.MerchantSettlementFileReq;
import com.paysphere.query.param.MerchantSettlementFileParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantSettlementRecordConverter {

    MerchantSettlementFileParam convertMerchantSettlementFileParam(MerchantSettlementFileReq req);

}
