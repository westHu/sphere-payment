package app.sphere.command.handler;

import app.sphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import app.sphere.command.dto.trade.result.MerchantResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.cmd.PaymentFinishCommand;
import app.sphere.command.cmd.SettleAccountUpdateSettleCommand;
import infrastructure.sphere.db.entity.TradePaymentLinkOrder;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.enums.TradeModeEnum;
import share.sphere.enums.TradePaymentLinkStatusEnum;
import share.sphere.enums.TradePaymentSourceEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import domain.sphere.repository.TradePaymentLinkOrderRepository;
import domain.sphere.repository.TradePaymentOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Component
public class PaymentFinish4PayHandler {

    @Resource
    TradePaymentOrderRepository tradePaymentOrderRepository;
    @Resource
    TradePaymentLinkOrderRepository tradePaymentLinkOrderRepository;
    @Resource
    SettleAccountCmdService settleAccountCmdService;

    /**
     * 代收支付结果
     */
    public void handlerPaymentFinish4Pay(PaymentFinishCommand command) {
        log.info("handlerPaymentFinish4Pay command={}", JSONUtil.toJsonStr(command));
        doPaymentFinish4Payment(command);
    }


    //------------------------------------------------------------------------------------------------------------

    /**
     * 代收支付完成
     */
    private boolean doPaymentFinish4Payment(PaymentFinishCommand command) {
        //校验参数Command
        checkCommand(command);

        //查询代收订单
        TradePaymentOrder order = queryPayOrder(command);

        //处理订单
        handlerPaymentOrder(command, order);

        //支付成功, 执行结算操作
        PaymentStatusEnum statusEnum = PaymentStatusEnum.codeToEnum(command.getPaymentStatus());
        log.info("handlerPaymentFinish4Pay statusEnum={}", statusEnum.name());
        if (PaymentStatusEnum.PAYMENT_SUCCESS.equals(statusEnum)) {
            handleSettle(order);
        }

        //API订单 执行商户回调消息
        TradePaymentSourceEnum sourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
        log.info("handlerPaymentFinish4Pay sourceEnum={}", sourceEnum.name());
        TradeCallBackDTO callBackDTO = buildPayOrderCallBackDTO(order, statusEnum, sourceEnum);

        return true;
    }

    /**
     * 校验参数Command
     */
    private void checkCommand(PaymentFinishCommand command) {
        String tradeNo = command.getTradeNo();
        //传入的状态也必须是终态
        boolean contains = PaymentStatusEnum.getFinalStatus().contains(command.getPaymentStatus());
        Assert.isTrue(contains, () -> new PaymentException("not final status. TradeNo to " + tradeNo));
    }

    /**
     * 查询订单
     */
    private TradePaymentOrder queryPayOrder(PaymentFinishCommand command) {
        String tradeNo = command.getTradeNo();
        QueryWrapper<TradePaymentOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last("LIMIT 1");
        TradePaymentOrder order = tradePaymentOrderRepository.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo));

        //下单成功后才操作, 否则需要重复消费
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        boolean equals = TradeStatusEnum.TRADE_SUCCESS.equals(tradeStatusEnum);
        Assert.isTrue(equals, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo));

        //原订单不能是终态
        boolean contains = PaymentStatusEnum.getFinalStatus().contains(order.getPaymentStatus());
        Assert.isFalse(contains, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo));
        return order;
    }

    /**
     * 其他正常订单的处理
     */
    private void handlerPaymentOrder(PaymentFinishCommand command, TradePaymentOrder order) {
        //更新订单支付状态、结果、时间
        UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
        payOrderUpdate.lambda()
                .set(TradePaymentOrder::getPaymentStatus, command.getPaymentStatus())
                .set(TradePaymentOrder::getPaymentResult, JSONUtil.toJsonStr(command))
                .set(TradePaymentOrder::getPayerInfo, order.getPayerInfo())
                .set(TradePaymentOrder::getPaymentFinishTime, order.getPaymentFinishTime())
                .eq(TradePaymentOrder::getId, order.getId());
        boolean update = tradePaymentOrderRepository.update(payOrderUpdate);
        log.info("handlerPaymentFinish4Pay update order result={}", update);

        //如果是PaymentLink
        boolean isPaymentLink = Optional.ofNullable(order.getSource())
                .map(e -> e.equals(TradePaymentSourceEnum.PAY_LINK.getCode()))
                .orElse(false);
        log.info("handlerPaymentFinish4Pay isPaymentLink? {}", isPaymentLink);
        if (isPaymentLink) {
            QueryWrapper<TradePaymentLinkOrder> linkOrderQuery = new QueryWrapper<>();
            linkOrderQuery.lambda().eq(TradePaymentLinkOrder::getLinkNo, order.getOrderNo()).last("LIMIT 1");
            TradePaymentLinkOrder paymentLinkOrder = tradePaymentLinkOrderRepository.getOne(linkOrderQuery);
            if (Objects.nonNull(paymentLinkOrder)) {
                TradePaymentLinkStatusEnum paymentLinkStatusEnum = Optional.of(command)
                        .map(PaymentFinishCommand::getPaymentStatus)
                        .map(TradePaymentLinkStatusEnum::codeToEnum)
                        .orElse(TradePaymentLinkStatusEnum.PAYMENT_LINK_SUCCESS);
                log.info("handlerPaymentFinish4Pay paymentLinkStatusEnum={}", paymentLinkStatusEnum.name());

                UpdateWrapper<TradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
                linkOrderUpdate.lambda()
                        .set(TradePaymentLinkOrder::getLinkStatus, paymentLinkStatusEnum.getCode())
                        .eq(TradePaymentLinkOrder::getId, paymentLinkOrder.getId());
                boolean linkUpdate = tradePaymentLinkOrderRepository.update(linkOrderUpdate);
                log.info("handlerPaymentFinish4Pay update paymentLink result={}", linkUpdate);
            }
        }
    }

    /**
     * 构建主动清结算参数
     */
    private void handleSettle(TradePaymentOrder order) {
        //参数
        BigDecimal amount = order.getAmount();
        BigDecimal merchantFee = order.getMerchantFee();
        BigDecimal merchantProfit = order.getMerchantProfit();
        BigDecimal accountAmount = order.getAccountAmount();
        BigDecimal channelCost = order.getChannelCost();
        BigDecimal platformProfit = order.getPlatformProfit();

        //如果结算需要的金额缺失
        if (Objects.isNull(amount)
                ||Objects.isNull(merchantFee)
                || Objects.isNull(merchantProfit)
                || Objects.isNull(accountAmount)
                || Objects.isNull(channelCost)
                || Objects.isNull(platformProfit)) {
            log.error("handleSettle exception {} {} {} {} {} {}",
                    amount, merchantFee, merchantProfit, accountAmount, channelCost, platformProfit);
            throw new PaymentException("PaymentFinish4PayHandler exception. some fees is null");
        }

        SettleAccountUpdateSettleCommand settleCommand = new SettleAccountUpdateSettleCommand();
        BeanUtils.copyProperties(order, settleCommand);
        settleAccountCmdService.handlerAccountSettlement(settleCommand);
    }

    /**
     * 构建回调消息体
     */
    private TradeCallBackDTO buildPayOrderCallBackDTO(TradePaymentOrder payOrder, PaymentStatusEnum statusEnum,
                                                      TradePaymentSourceEnum sourceEnum) {
        String finishPaymentUrl = Optional.of(payOrder).map(TradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .map(MerchantResultDTO::getFinishPaymentUrl)
                .orElse(null);

        TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
        bodyDTO.setTradeNo(payOrder.getTradeNo());
        bodyDTO.setOrderNo(payOrder.getOrderNo());
        bodyDTO.setMerchantId(payOrder.getMerchantId());
        bodyDTO.setMerchantName(payOrder.getMerchantName());
        bodyDTO.setPaymentMethod(payOrder.getPaymentMethod());
        bodyDTO.setStatus(statusEnum.getName());
        bodyDTO.setTransactionTime(null);

        TradeCallBackMoneyDTO moneyDTO = new TradeCallBackMoneyDTO();
        moneyDTO.setCurrency(payOrder.getCurrency());
        moneyDTO.setAmount(payOrder.getAmount());
        bodyDTO.setMoney(moneyDTO);

        TradeCallBackDTO callBackDTO = new TradeCallBackDTO();
        callBackDTO.setMode(TradeModeEnum.PRODUCTION.getMode());
        callBackDTO.setSource(sourceEnum.name());
        callBackDTO.setUrl(finishPaymentUrl);
        callBackDTO.setBody(bodyDTO);
        return callBackDTO;
    }
}
