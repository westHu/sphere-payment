package app.sphere.query;


import app.sphere.query.dto.MerchantWithdrawConfigDTO;
import app.sphere.query.param.MerchantIdParam;

public interface MerchantWithdrawConfigQueryService {

    MerchantWithdrawConfigDTO getMerchantWithdrawConfig(MerchantIdParam param);
}
