package app.sphere.query;


import app.sphere.query.dto.MerchantPayoutConfigDTO;
import app.sphere.query.param.MerchantIdParam;

public interface MerchantPayoutConfigQueryService {

    MerchantPayoutConfigDTO getMerchantPayoutConfig(MerchantIdParam param);

}
