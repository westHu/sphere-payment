package com.paysphere.convert;

import com.paysphere.controller.request.SettleAccountFlowPageReq;
import com.paysphere.controller.request.SettleAmountReq;
import com.paysphere.controller.response.SettleAccountFlowVO;
import com.paysphere.db.entity.SettleAccountFlow;
import com.paysphere.query.param.SettleAccountFlowPageParam;
import com.paysphere.query.param.SettleAmountParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface SettleFlowConverter {

    SettleAccountFlowPageParam convertAccountFlowPageParam(SettleAccountFlowPageReq req);

    List<SettleAccountFlowVO> convertAccountFlowVOList(List<SettleAccountFlow> records);

    SettleAmountParam convertSettleAmountParam(SettleAmountReq req);
}
