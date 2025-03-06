package com.paysphere.convert;


import com.paysphere.command.cmd.SettleFileJobCommand;
import com.paysphere.command.cmd.SettleOrderFrozenCommand;
import com.paysphere.controller.request.SettleOrderFrozenReq;
import com.paysphere.controller.request.SettleOrderPageReq;
import com.paysphere.controller.request.SettleOrderReq;
import com.paysphere.db.entity.SettleOrder;
import com.paysphere.job.param.SettleFileJobParam;
import com.paysphere.query.dto.SettleOrderDTO;
import com.paysphere.query.param.SettleOrderPageParam;
import com.paysphere.query.param.SettleOrderParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface SettleConverter {


    SettleOrderPageParam convertSettleOrderPageParam(SettleOrderPageReq req);

    List<SettleOrderDTO> convertSettleOrderDTOList(List<SettleOrder> records);

    SettleOrderFrozenCommand convertSettleOrderFrozenCommand(SettleOrderFrozenReq req);

    SettleOrderParam convertSettleOrderParam(SettleOrderReq req);

    SettleOrderDTO convertSettleOrderDTO(SettleOrder settleOrder);

    SettleFileJobCommand convertSettleFileJobCommand(SettleFileJobParam param);
}
