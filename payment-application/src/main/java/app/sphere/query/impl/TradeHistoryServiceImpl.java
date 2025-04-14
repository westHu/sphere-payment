package app.sphere.query.impl;

import app.sphere.query.param.TradeOrderStatusInquiryParam;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import app.sphere.query.TradeHistoryService;
import app.sphere.query.dto.TradeOrderStatusInquiryDTO;
import domain.sphere.repository.TradePaymentOrderRepository;
import domain.sphere.repository.TradePayoutOrderRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static share.sphere.TradeConstant.LIMIT_1;

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
