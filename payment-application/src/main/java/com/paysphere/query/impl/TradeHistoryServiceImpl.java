package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.cache.RedisService;
import com.paysphere.db.entity.TradePaymentOrder;
import com.paysphere.db.entity.TradePayoutOrder;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.query.TradeHistoryService;
import com.paysphere.query.dto.TradeOrderStatusInquiryDTO;
import com.paysphere.query.param.TradeOrderStatusInquiryParam;
import com.paysphere.repository.TradePaymentOrderService;
import com.paysphere.repository.TradePayoutOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static com.paysphere.TradeConstant.CACHE_ORDER_STATUS;
import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class TradeHistoryServiceImpl extends AbstractTradeOrderQueryServiceImpl
        implements TradeHistoryService {

    @Resource
    TradePaymentOrderService tradePaymentOrderService;
    @Resource
    TradePayoutOrderService tradePayoutOrderService;
    @Resource
    RocketMqProducer rocketMqProducer;
    @Resource
    RedisService redisService;


    @Override
    public TradeOrderStatusInquiryDTO inquiryOrderStatus(TradeOrderStatusInquiryParam param) {
        log.info("inquiryOrderStatus param={}", JSONUtil.toJsonStr(param));
        Integer tradeType = param.getTradeType();
        String orderNo = param.getOrderNo();
        String tradeNo = param.getTradeNo();
        if (Objects.isNull(tradeType) && StringUtils.isAllBlank(orderNo, tradeNo)) {
            throw new PaymentException("Invalid parameter");
        }

        TradeTypeEnum tradeTypeEnum;
        if (Objects.nonNull(tradeType) && StringUtils.isNotBlank(orderNo)) {
            tradeTypeEnum = TradeTypeEnum.codeToEnum(param.getTradeType());
            tradeNo = null;
        } else if (StringUtils.isNotBlank(tradeNo)) {
            tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(param.getTradeNo());
            orderNo = null;
        } else {
            throw new PaymentException("Invalid parameter");
        }

        // 缓存是否存在
        String redisKey = String.join("-", CACHE_ORDER_STATUS, tradeTypeEnum.name(), orderNo, tradeNo);
        Object value = redisService.get(redisKey);
        TradeOrderStatusInquiryDTO inquiryDTO = Optional.ofNullable(value).map(Object::toString)
                .map(e -> JSONUtil.toBean(e, TradeOrderStatusInquiryDTO.class))
                .orElse(null);
        if (Objects.nonNull(inquiryDTO)) {
            return inquiryDTO;
        }

        TradeOrderStatusInquiryDTO dto = new TradeOrderStatusInquiryDTO();
        // 数据库是否存在
        log.info("inquiryOrderStatus tradeTypeEnum={}", tradeTypeEnum.name());
        if (TradeTypeEnum.PAYMENT.equals(tradeTypeEnum)) {
            QueryWrapper<TradePaymentOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(StringUtils.isNotBlank(orderNo), TradePaymentOrder::getOrderNo, orderNo)
                    .eq(StringUtils.isNotBlank(tradeNo), TradePaymentOrder::getTradeNo, tradeNo)
                    .last(LIMIT_1);
            TradePaymentOrder payOrder = tradePaymentOrderService.getOne(queryWrapper);
            if (Objects.nonNull(payOrder)) {
                dto.setTradeType(tradeTypeEnum.getCode());
                dto.setOrderNo(payOrder.getOrderNo());
                dto.setTradeNo(payOrder.getTradeNo());
                // 设置支付方式金额
//                MoneyDTO moneyDTO = new MoneyDTO();
//                moneyDTO.setCurrency(payOrder.getCurrency());
//                moneyDTO.setAmount(payOrder.getAmount());
//                dto.setPaymentMethod(payOrder.getPaymentMethod());
//                dto.setMoney(moneyDTO);
//                dto.setStatus(PaymentStatusEnum.codeToEnum(payOrder.getPaymentStatus()).getMerchantStatus());
                // 如果失败，则返回失败原因
                TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(payOrder.getTradeStatus());
                if (TradeStatusEnum.TRADE_FAILED.equals(tradeStatusEnum)) {
                    String errorMsg = getPayErrorMsg(payOrder.getTradeResult());
                    dto.setRemark(errorMsg);
                }
            }
        } else if (TradeTypeEnum.PAYOUT.equals(tradeTypeEnum)) {
            QueryWrapper<TradePayoutOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(StringUtils.isNotBlank(orderNo), TradePayoutOrder::getOuterNo, orderNo)
                    .eq(StringUtils.isNotBlank(tradeNo), TradePayoutOrder::getTradeNo, tradeNo)
                    .last(LIMIT_1);
            TradePayoutOrder cashOrder = tradePayoutOrderService.getOne(queryWrapper);
            if (Objects.nonNull(cashOrder)) {
                dto.setTradeType(tradeTypeEnum.getCode());
                dto.setOrderNo(cashOrder.getOuterNo());
                dto.setTradeNo(cashOrder.getTradeNo());
                // 设置支付方式金额
//                MoneyDTO moneyDTO = new MoneyDTO();
//                moneyDTO.setCurrency(cashOrder.getCurrency());
//                moneyDTO.setAmount(cashOrder.getAmount());
//                dto.setPaymentMethod(cashOrder.getPaymentMethod());
//                dto.setMoney(moneyDTO);
//                dto.setStatus(PaymentStatusEnum.codeToEnum(cashOrder.getPaymentStatus()).getMerchantStatus());
                // 如果失败，则返回失败原因
                TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(cashOrder.getTradeStatus());
                if (TradeStatusEnum.TRADE_FAILED.equals(tradeStatusEnum)) {
                    String errorMsg = getCashErrorMsg(cashOrder.getTradeResult());
                    dto.setRemark(errorMsg);
                }
            }
        } else {
            dto.setRemark(ExceptionCode.UNSUPPORTED_TRADE_TYPE.getMessage());
        }

        // 如果是终态存1小时，非终态存10s
        String status = dto.getStatus();
        int expired = TradeStatusEnum.getFinalMerchantStatus().contains(status) ? 60 * 60 : 10;
        redisService.set(redisKey, JSONUtil.toJsonStr(dto), expired);

        return dto;
    }


}
