package com.paysphere.query;


import com.paysphere.query.dto.MerchantConfigDTO;
import com.paysphere.query.dto.MerchantPaymentLinkSettingDTO;
import com.paysphere.query.param.MerchantIdParam;

public interface MerchantConfigQueryService {

    MerchantConfigDTO getMerchantConfig(MerchantIdParam param);

    MerchantPaymentLinkSettingDTO getPaymentLinkSetting(MerchantIdParam param);
}
