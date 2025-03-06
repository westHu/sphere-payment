package com.paysphere.query;


import com.paysphere.query.dto.MerchantWithdrawConfigDTO;
import com.paysphere.query.param.MerchantIdParam;

public interface MerchantWithdrawConfigQueryService {

    MerchantWithdrawConfigDTO getMerchantWithdrawConfig(MerchantIdParam param);
}
