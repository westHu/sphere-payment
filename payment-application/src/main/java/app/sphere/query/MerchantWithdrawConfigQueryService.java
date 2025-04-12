package app.sphere.query;


import app.sphere.query.param.MerchantIdParam;
import app.sphere.query.dto.MerchantWithdrawConfigDTO;

public interface MerchantWithdrawConfigQueryService {

    MerchantWithdrawConfigDTO getMerchantWithdrawConfig(MerchantIdParam param);
}
