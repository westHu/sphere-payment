package app.sphere.query.impl;

import app.sphere.query.TradeHistoryService;
import app.sphere.query.dto.TradeOrderStatusInquiryDTO;
import app.sphere.query.param.TradeOrderStatusInquiryParam;
import cn.hutool.json.JSONUtil;
import domain.sphere.repository.TradePaymentOrderRepository;
import domain.sphere.repository.TradePayoutOrderRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TradeHistoryServiceImpl extends AbstractTradeOrderQueryServiceImpl
        implements TradeHistoryService {

    @Resource
    TradePaymentOrderRepository tradePaymentOrderRepository;
    @Resource
    TradePayoutOrderRepository tradePayoutOrderRepository;

    @Override
    public TradeOrderStatusInquiryDTO inquiryOrderStatus(TradeOrderStatusInquiryParam param) {
        log.info("inquiryOrderStatus param={}", JSONUtil.toJsonStr(param));
        return null;
    }

}
