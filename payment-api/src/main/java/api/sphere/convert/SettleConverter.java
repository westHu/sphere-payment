package api.sphere.convert;


import api.sphere.controller.request.*;
import api.sphere.job.param.SettleFileJobParam;
import app.sphere.command.cmd.*;
import app.sphere.query.dto.SettleOrderDTO;
import app.sphere.query.param.SettleOrderPageParam;
import app.sphere.query.param.SettleOrderParam;
import infrastructure.sphere.db.entity.SettleOrder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface SettleConverter {

    SettleOrderPageParam convertSettleOrderPageParam(SettleOrderPageReq req);

    List<SettleOrderDTO> convertSettleOrderDTOList(List<SettleOrder> records);

    SettleOrderParam convertSettleOrderParam(SettleOrderReq req);

    SettleOrderDTO convertSettleOrderDTO(SettleOrder settleOrder);

    SettleFileJobCommand convertSettleFileJobCommand(SettleFileJobParam param);

    SettleRefundCmd convertRefundCmd(SettleRefundReq req);

    SettleSupplementCmd convertSupplementCmd(SettleSupplementReq req);

    SettleSupplementCmd convertSettleSupplementCmd(SettleSupplementReq req);

    SettleRefundCmd convertSettleRefundCmd(SettleRefundReq req);
}
