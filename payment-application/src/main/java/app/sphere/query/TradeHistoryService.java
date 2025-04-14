package app.sphere.query;

import app.sphere.query.dto.TradeOrderStatusInquiryDTO;
import app.sphere.query.param.TradeOrderStatusInquiryParam;

public interface TradeHistoryService {

    TradeOrderStatusInquiryDTO inquiryOrderStatus(TradeOrderStatusInquiryParam param);

}
