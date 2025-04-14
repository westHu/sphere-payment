package app.sphere.query;


import app.sphere.query.dto.MerchantConfigDTO;
import app.sphere.query.dto.MerchantPaymentLinkSettingDTO;
import app.sphere.query.param.MerchantIdParam;

public interface MerchantConfigQueryService {

    MerchantConfigDTO getMerchantConfig(MerchantIdParam param);

    MerchantPaymentLinkSettingDTO getPaymentLinkSetting(MerchantIdParam param);
}
