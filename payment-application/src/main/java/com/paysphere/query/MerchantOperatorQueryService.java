package com.paysphere.query;

import com.paysphere.db.entity.MerchantOperator;
import com.paysphere.query.dto.MerchantOperatorDTO;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.param.MerchantOperatorListParam;
import com.paysphere.query.param.MerchantOperatorPageParam;

import java.util.List;

public interface MerchantOperatorQueryService {

    List<MerchantOperator> getMerchantOperatorList(MerchantOperatorListParam param);

    PageDTO<MerchantOperatorDTO> pageMerchantOperatorList(MerchantOperatorPageParam param);
}
