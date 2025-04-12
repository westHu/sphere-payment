package app.sphere.query;

import app.sphere.query.dto.MerchantOperatorDTO;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.param.MerchantOperatorListParam;
import app.sphere.query.param.MerchantOperatorPageParam;
import infrastructure.sphere.db.entity.MerchantOperator;

import java.util.List;

public interface MerchantOperatorQueryService {

    List<MerchantOperator> getMerchantOperatorList(MerchantOperatorListParam param);

    PageDTO<MerchantOperatorDTO> pageMerchantOperatorList(MerchantOperatorPageParam param);
}
