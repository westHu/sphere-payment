package app.sphere.query;

import app.sphere.query.dto.MerchantChannelConfigListDTO;
import app.sphere.query.param.MerchantChannelConfigListParam;

public interface MerchantChannelConfigQueryService {

    MerchantChannelConfigListDTO getMerchantChannelConfigList(MerchantChannelConfigListParam param);

}
