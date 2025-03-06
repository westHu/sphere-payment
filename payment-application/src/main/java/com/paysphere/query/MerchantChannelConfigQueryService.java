package com.paysphere.query;

import com.paysphere.query.dto.MerchantChannelConfigListDTO;
import com.paysphere.query.param.MerchantChannelConfigListParam;

public interface MerchantChannelConfigQueryService {

    MerchantChannelConfigListDTO getMerchantChannelConfigList(MerchantChannelConfigListParam param);

}
