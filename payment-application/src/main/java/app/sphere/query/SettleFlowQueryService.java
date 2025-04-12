package app.sphere.query;

import app.sphere.query.param.SettleAccountFlowPageParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.SettleAccountFlow;

public interface SettleFlowQueryService {

    Page<SettleAccountFlow> pageAccountFlowList(SettleAccountFlowPageParam param);

    String exportAccountFlowList(SettleAccountFlowPageParam param);

}
