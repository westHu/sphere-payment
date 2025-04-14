package app.sphere.query;


import app.sphere.query.param.MerchantIdParam;
import app.sphere.query.dto.MerchantPayoutConfigDTO;

public interface MerchantPayoutConfigQueryService {

    MerchantPayoutConfigDTO getMerchantPayoutConfig(MerchantIdParam param);

}
