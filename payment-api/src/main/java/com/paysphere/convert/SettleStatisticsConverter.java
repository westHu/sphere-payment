package com.paysphere.convert;

import com.paysphere.controller.request.SettleTimelyStatisticsIndexReq;
import com.paysphere.query.param.SettleTimelyStatisticsIndexParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface SettleStatisticsConverter {

    SettleTimelyStatisticsIndexParam convertSettleTimelyStatisticsIndexParam(SettleTimelyStatisticsIndexReq req);
}
