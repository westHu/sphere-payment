package app.sphere.query.impl;

import app.sphere.query.SandboxTradeHistoryService;
import domain.sphere.repository.TradeSandboxPaymentOrderRepository;
import domain.sphere.repository.TradeSandboxPayoutOrderRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SandboxTradeHistoryServiceImpl implements SandboxTradeHistoryService {

    @Resource
    TradeSandboxPaymentOrderRepository tradeSandboxPaymentOrderRepository;
    @Resource
    TradeSandboxPayoutOrderRepository tradeSandboxPayoutOrderRepository;

   /* @Override
    public TradeOrderStatusInquiryDTO inquiryOrderStatus(TradeOrderStatusInquiryParam param) {
        log.info("inquiryOrderStatus param={}", JSONUtil.toJsonStr(param));
        
        Integer tradeType = param.getTradeType();
        String orderNo = param.getOrderNo();
        String tradeNo = param.getTradeNo();
        if (Objects.isNull(tradeType) && StringUtils.isAllBlank(orderNo, tradeNo)) {
            throw new PaymentException("Invalid parameter");
        }

        TradeOrderStatusInquiryDTO dto = new TradeOrderStatusInquiryDTO();
        dto.setTradeType(tradeType);
        dto.setOrderNo(orderNo);
        dto.setTradeNo(tradeNo);

        TradeTypeEnum tradeTypeEnum;
        if (Objects.nonNull(tradeType) && StringUtils.isNotBlank(orderNo)) {
            tradeTypeEnum = TradeTypeEnum.codeToEnum(tradeType);
            tradeNo = null;
        } else if (StringUtils.isNotBlank(tradeNo)) {
            tradeNo = tradeNo.replace(TradeConstant.EXPERIENCE_CASHIER_PREFIX, "");
            tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(tradeNo);
            orderNo = null;
        } else {
            return dto;
        }

        log.info("inquiryOrderStatus tradeTypeEnum={}", tradeTypeEnum.name());
        if (TradeTypeEnum.PAYMENT.equals(tradeTypeEnum)) {
            QueryWrapper<SandboxTradePaymentOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(StringUtils.isNotBlank(tradeNo), SandboxTradePaymentOrder::getTradeNo, tradeNo)
                    .eq(StringUtils.isNotBlank(orderNo), SandboxTradePaymentOrder::getOrderNo, orderNo)
                    .last(LIMIT_SQL);
            SandboxTradePaymentOrder payOrder = sandboxTradePayOrderService.getOne(queryWrapper);
            if (Objects.nonNull(payOrder)) {
                // 设置支付方式金额
                MoneyDTO moneyDTO = new MoneyDTO();
                moneyDTO.setCurrency(payOrder.getCurrency());
                moneyDTO.setAmount(payOrder.getAmount());
                dto.setPaymentMethod(payOrder.getPaymentMethod());
                dto.setMoney(moneyDTO);

                PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(payOrder.getPaymentStatus());
//                dto.setStatus(paymentStatusEnum.getMerchantStatus());
                // 如果失败，则返回失败原因
                TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(payOrder.getTradeStatus());
                if (TradeStatusEnum.TRADE_FAILED.equals(tradeStatusEnum)) {
                    String errorMsg = getErrorMsg(payOrder.getTradeResult());
                    dto.setRemark(errorMsg);
                }
            }
        } else if (TradeTypeEnum.CASH.equals(tradeTypeEnum)) {
            QueryWrapper<SandboxTradePayoutOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(StringUtils.isNotBlank(tradeNo), SandboxTradePayoutOrder::getTradeNo, tradeNo)
                    .eq(StringUtils.isNotBlank(orderNo), SandboxTradePayoutOrder::getOrderNo, orderNo)
                    .last(LIMIT_SQL);
            SandboxTradePayoutOrder cashOrder = sandboxTradeCashOrderService.getOne(queryWrapper);
            if (Objects.nonNull(cashOrder)) {
                // 设置支付方式金额
                MoneyDTO moneyDTO = new MoneyDTO();
                moneyDTO.setCurrency(cashOrder.getCurrency());
                moneyDTO.setAmount(cashOrder.getAmount());
                dto.setPaymentMethod(cashOrder.getPaymentMethod());
                dto.setMoney(moneyDTO);

//                dto.setStatus(PaymentStatusEnum.codeToEnum(cashOrder.getPaymentStatus()).getMerchantStatus());
                // 如果失败，则返回失败原因
                TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(cashOrder.getTradeStatus());
                if (TradeStatusEnum.TRADE_FAILED.equals(tradeStatusEnum)) {
                    String errorMsg = getErrorMsg(cashOrder.getTradeResult());
                    dto.setRemark(errorMsg);
                }
            }
        }

        return dto;
    }*/
}
