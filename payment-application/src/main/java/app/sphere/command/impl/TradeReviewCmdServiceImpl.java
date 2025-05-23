package app.sphere.command.impl;

import app.sphere.assembler.ApplicationConverter;
import app.sphere.command.*;
import app.sphere.command.cmd.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import share.sphere.enums.TradeTypeEnum;

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
    ThreadPoolTaskExecutor threadPoolTaskExecutor;


    @Override
    public boolean executeTradeReview(TradeReviewCommand command) {
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(command.getTradeNo());
        log.info("executeTradeReview tradeTypeEnum={}", tradeTypeEnum);

        try {
            // 出款
            if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {
                TradePayoutReviewCommand cCommand = applicationConverter.convertTradeCashReviewCommand(command);
                threadPoolTaskExecutor.execute(() -> tradePayoutOrderCmdService.executePayoutReview(cCommand));
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
        }
        return true;
    }
}
