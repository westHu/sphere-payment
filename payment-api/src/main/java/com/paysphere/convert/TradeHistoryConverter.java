package com.paysphere.convert;


import com.paysphere.controller.request.HistoryDetailReq;
import com.paysphere.controller.request.HistoryListReq;
import com.paysphere.controller.request.TradeLarkInquiryDecryptReq;
import com.paysphere.controller.request.TradeOrderStatusInquiryReq;
import com.paysphere.query.param.HistoryDetailParam;
import com.paysphere.query.param.HistoryListParam;
import com.paysphere.query.param.TradeLarkInquiryDecryptParam;
import com.paysphere.query.param.TradeOrderStatusInquiryParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface TradeHistoryConverter {

    HistoryListParam convertHistoryListParam(HistoryListReq req);

    HistoryDetailParam convertHistoryDetailParam(HistoryDetailReq req);

    TradeOrderStatusInquiryParam convertTradeOrderStatusInquiryParam(TradeOrderStatusInquiryReq req);

    TradeLarkInquiryDecryptParam convertTradeLarkInquiryDecryptParam(TradeLarkInquiryDecryptReq req);
}
