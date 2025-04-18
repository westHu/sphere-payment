package app.sphere.query;

import app.sphere.query.dto.SettleGroupDTO;
import app.sphere.query.param.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.SettleOrder;

import java.util.List;

public interface SettleOrderQueryService {

    List<SettleGroupDTO> groupSettleList(SettleGroupListParam param);

    Page<SettleOrder> pageSettleOrderList(SettleOrderPageParam param);

    SettleOrder getSettleOrder(SettleOrderParam param);
}
