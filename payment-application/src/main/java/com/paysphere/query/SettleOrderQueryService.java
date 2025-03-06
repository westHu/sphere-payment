package com.paysphere.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.SettleOrder;
import com.paysphere.query.dto.SettleGroupDTO;
import com.paysphere.query.param.SettleGroupListParam;
import com.paysphere.query.param.SettleOrderPageParam;
import com.paysphere.query.param.SettleOrderParam;

import java.util.List;

public interface SettleOrderQueryService {

    List<SettleGroupDTO> groupSettleList(SettleGroupListParam param);

    Page<SettleOrder> pageSettleOrderList(SettleOrderPageParam param);

    SettleOrder getSettleOrder(SettleOrderParam param);
}
