package com.paysphere.query;


import com.paysphere.query.dto.MerchantPayoutConfigDTO;
import com.paysphere.query.param.MerchantIdParam;

public interface MerchantPayoutConfigQueryService {

    MerchantPayoutConfigDTO getMerchantPayoutConfig(MerchantIdParam param);

}
