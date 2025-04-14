package app.sphere.command.impl;

import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.SettleTransferCmdService;
import app.sphere.command.cmd.SettleAccountUpdateTransferCommand;
import app.sphere.command.cmd.SettleTransferCommand;
import app.sphere.command.dto.SettleResultDTO;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import domain.sphere.repository.SettleOrderRepository;
import infrastructure.sphere.db.entity.SettleOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.*;
import share.sphere.exception.PaymentException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Slf4j
@Service
public class SettleTransferCmdServiceImpl implements SettleTransferCmdService {

    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettleOrderRepository settleOrderRepository;


    /**
     * 处理转账操作的资金处理
     */

    @Override
    public void handlerTransfer(SettleTransferCommand command) {
        log.info("handler transfer command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();

        //校验是否存在, 应该不存在, 否则异常
        verifySettleOrder(command.getTradeNo());

        SettleResultDTO resultDTO = new SettleResultDTO();
        try {
            doTransfer(command);
            resultDTO.setSuccess(true);
        } catch (Exception e) {
            log.error("handler transfer tradeNo={} exception:", tradeNo, e);
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "Handler transfer failed";
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);
            throw e;
        } finally {
            notfiySettleResult(tradeNo, resultDTO);
        }
    }

    /**
     * 校验是否存在
     */
    private void verifySettleOrder(String tradeNo) {
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("trade_no as tradeNo");
        queryWrapper.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        SettleOrder settleOrder = settleOrderRepository.getOne(queryWrapper);
        if (Objects.nonNull(settleOrder)) {
            throw new PaymentException("transfer settle order has exist. " + tradeNo);
        }
    }

    /**
     * 转账操作的资金操作
     */
    private void doTransfer(SettleTransferCommand command) {
        log.info("doTransfer command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();

        //转出商户
        String transferOutMerchantId = null; //accountRecord.getMerchantId();
        String transferOutMerchantName = null ; //accountRecord.getMerchantName();
        String transferOutMerchantAccountNo = null; //accountRecord.getMerchantAccountNo();

        //转入商户
        String transferToMerchantId = command.getTransferToMerchantId();
        String transferToMerchantName = command.getTransferToMerchantName();
        String transferToAccount = command.getTransferToAccount();

        //执行资金转账操作
        SettleAccountUpdateTransferCommand transferCommand = new SettleAccountUpdateTransferCommand();
        transferCommand.setFromMerchantId(transferOutMerchantId);
        transferCommand.setFromMerchantName(transferOutMerchantName);
//        transferCommand.setAccountNo(transferOutMerchantAccountNo);
//        transferCommand.setAccountOptType(AccountOptTypeEnum.TRANSFER);
//        transferCommand.setCurrency(accountRecord.getCurrency());
//        transferCommand.setAmount(accountRecord.getMerchantAmount());
        transferCommand.setMerchantFee(BigDecimal.ZERO);
        transferCommand.setMerchantProfit(BigDecimal.ZERO);
        transferCommand.setChannelCost(BigDecimal.ZERO);
        transferCommand.setAccountAmount(BigDecimal.ZERO);
        transferCommand.setPlatformProfit(BigDecimal.ZERO);
//        transferCommand.setTradeNo(accountRecord.getTradeNo());
//        transferCommand.setDeductionType(DeductionTypeEnum.DEDUCTION_INTERNAL.getCode()); //默认
//        transferCommand.setTransferToMerchantId(transferToMerchantId);
//        transferCommand.setTransferToMerchantName(transferToMerchantName);
//        transferCommand.setTransferToAccount(transferToAccount);
        settleAccountCmdService.handlerAccountTransfer(transferCommand);

        //保存结算流水
        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setSettleType(SettleTypeEnum.T0.name());  //解冻、出款 都是D0
        settleOrder.setSettleTime(LocalTime.now().format(TradeConstant.DF_1));  //解冻、出款 转账 当时时刻
//        settleOrder.setTradeNo(accountRecord.getTradeNo());
        settleOrder.setTradeType(TradeTypeEnum.TRANSFER.getCode());
//        settleOrder.setTradeTime(accountRecord.getRecordTime());
//        settleOrder.setPaymentFinishTime(accountRecord.getRecordTime());
        settleOrder.setSettleStatus(SettleStatusEnum.SETTLE_SUCCESS.getCode());
        settleOrder.setMerchantId(transferOutMerchantId);
        settleOrder.setMerchantName(transferOutMerchantName);
        settleOrder.setAccountNo(transferOutMerchantAccountNo);
        settleOrder.setChannelCode(null);
        settleOrder.setChannelName(null);
        settleOrder.setPaymentMethod(null);

        settleOrder.setDeductionType(DeductionTypeEnum.DEDUCTION_INTERNAL.getCode());
//        settleOrder.setCurrency(accountRecord.getCurrency());
//        settleOrder.setAmount(accountRecord.getMerchantAmount());
        settleOrder.setMerchantFee(BigDecimal.ZERO);
        settleOrder.setMerchantProfit(BigDecimal.ZERO);
        settleOrder.setChannelCost(BigDecimal.ZERO);
        settleOrder.setPlatformProfit(BigDecimal.ZERO);
        settleOrder.setAccountAmount(BigDecimal.ZERO);
        settleOrder.setActualSettleTime(System.currentTimeMillis());
        settleOrder.setCreateTime(LocalDateTime.now());
        settleOrder.setAttribute("{}");
        settleOrderRepository.save(settleOrder);
    }

    /**
     * mq发送消息
     */
    private void notfiySettleResult(String tradeNo, SettleResultDTO resultDTO) {

    }

}
