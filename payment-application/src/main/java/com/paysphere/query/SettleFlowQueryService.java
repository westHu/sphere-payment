package com.paysphere.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.SettleAccountFlow;
import com.paysphere.query.param.SettleAccountFlowPageParam;

public interface SettleFlowQueryService {

    Page<SettleAccountFlow> pageAccountFlowList(SettleAccountFlowPageParam param);

    String exportAccountFlowList(SettleAccountFlowPageParam param);

}
