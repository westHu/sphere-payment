package api.sphere.convert;

import api.sphere.controller.request.SettleTimelyStatisticsIndexReq;
import app.sphere.query.param.SettleTimelyStatisticsIndexParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface SettleStatisticsConverter {

    SettleTimelyStatisticsIndexParam convertSettleTimelyStatisticsIndexParam(SettleTimelyStatisticsIndexReq req);
}
