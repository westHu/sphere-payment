package com.paysphere.query;

import com.paysphere.query.dto.TradeOrderStatusInquiryDTO;
import com.paysphere.query.param.TradeOrderStatusInquiryParam;

public interface TradeHistoryService {

    TradeOrderStatusInquiryDTO inquiryOrderStatus(TradeOrderStatusInquiryParam param);

}
