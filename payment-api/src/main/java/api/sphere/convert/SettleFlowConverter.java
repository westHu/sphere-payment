package api.sphere.convert;

import api.sphere.controller.request.SettleAccountFlowPageReq;
import api.sphere.controller.response.SettleAccountFlowVO;
import app.sphere.query.param.SettleAccountFlowPageParam;
import infrastructure.sphere.db.entity.SettleAccountFlow;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface SettleFlowConverter {

    SettleAccountFlowPageParam convertAccountFlowPageParam(SettleAccountFlowPageReq req);

    List<SettleAccountFlowVO> convertAccountFlowVOList(List<SettleAccountFlow> records);

}
