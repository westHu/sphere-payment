package api.sphere.convert;


import api.sphere.controller.request.TradeOrderStatusInquiryReq;
import app.sphere.query.param.TradeOrderStatusInquiryParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface TradeHistoryConverter {

    TradeOrderStatusInquiryParam convertTradeOrderStatusInquiryParam(TradeOrderStatusInquiryReq req);

}
