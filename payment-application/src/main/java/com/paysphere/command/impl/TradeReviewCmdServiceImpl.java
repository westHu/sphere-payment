package com.paysphere.command.impl;

import com.paysphere.assembler.ApplicationConverter;
import com.paysphere.command.TradePayoutOrderCmdService;
import com.paysphere.command.TradeRechargeOrderCmdService;
import com.paysphere.command.TradeReviewCmdService;
import com.paysphere.command.TradeTransferOrderCmdService;
import com.paysphere.command.TradeWithdrawOrderCmdService;
import com.paysphere.command.cmd.TradeCashReviewCommand;
import com.paysphere.command.cmd.TradeRechargeReviewCommand;
import com.paysphere.command.cmd.TradeReviewCommand;
import com.paysphere.command.cmd.TradeTransferReviewCommand;
import com.paysphere.command.cmd.TradeWithdrawReviewCommand;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.mq.RocketMqProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TradeReviewCmdServiceImpl implements TradeReviewCmdService {

    @Resource
    TradePayoutOrderCmdService tradePayoutOrderCmdService;
    @Resource
    TradeTransferOrderCmdService tradeTransferOrderCmdService;
    @Resource
    TradeRechargeOrderCmdService tradeRechargeOrderCmdService;
    @Resource
    TradeWithdrawOrderCmdService tradeWithdrawOrderCmdService;
    @Resource
    ApplicationConverter applicationConverter;
    @Resource
    RocketMqProducer rocketMqProducer;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;


    @Override
    public boolean executeTradeReview(TradeReviewCommand command) {
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(command.getTradeNo());
        log.info("executeTradeReview tradeTypeEnum={}", tradeTypeEnum);

        try {
            // 出款
            if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {
                TradeCashReviewCommand cCommand = applicationConverter.convertTradeCashReviewCommand(command);
                threadPoolTaskExecutor.execute(() -> tradePayoutOrderCmdService.executeCashReview(cCommand));
            }
            // 转账
            else if (tradeTypeEnum.equals(TradeTypeEnum.TRANSFER)) {
                TradeTransferReviewCommand tCommand = applicationConverter.convertTradeTransferReviewCommand(command);
                threadPoolTaskExecutor.execute(() -> tradeTransferOrderCmdService.executeTransferReview(tCommand));
            }
            // 充值
            else if (tradeTypeEnum.equals(TradeTypeEnum.RECHARGE)) {
                TradeRechargeReviewCommand rCommand = applicationConverter.convertTradeRechargeReviewCommand(command);
                threadPoolTaskExecutor.execute(() -> tradeRechargeOrderCmdService.executeRechargeReview(rCommand));
            }
            // 提现
            else if (tradeTypeEnum.equals(TradeTypeEnum.WITHDRAW)) {
                TradeWithdrawReviewCommand wCommand = applicationConverter.convertTradeWithdrawReviewCommand(command);
                threadPoolTaskExecutor.execute(() -> tradeWithdrawOrderCmdService.executeWithdrawReview(wCommand));
            }
        } catch (Exception e) {
            log.error("execute trade review exception", e);
            rocketMqProducer.syncSendExceptionMessage("Failed to handler order review." + e.getMessage());
        }
        return true;
    }
}
