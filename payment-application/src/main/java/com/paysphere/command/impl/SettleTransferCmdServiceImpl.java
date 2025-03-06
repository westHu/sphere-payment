package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.paysphere.TradeConstant;
import com.paysphere.command.SettleAccountCmdService;
import com.paysphere.command.SettleTransferCmdService;
import com.paysphere.command.cmd.SettleAccountUpdateTransferCommand;
import com.paysphere.command.cmd.SettleTransferCommand;
import com.paysphere.command.dto.AccountDTO;
import com.paysphere.command.dto.SettleResultDTO;
import com.paysphere.db.entity.SettleOrder;
import com.paysphere.enums.AccountOptTypeEnum;
import com.paysphere.enums.DeductionTypeEnum;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.SettleTypeEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.PaymentException;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.mq.dto.SettleFinishAccountMessageDTO;
import com.paysphere.mq.dto.SettleFinishMessageDTO;
import com.paysphere.repository.SettleOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SettleTransferCmdServiceImpl implements SettleTransferCmdService {

    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettleOrderService settleOrderService;
    @Resource
    RocketMqProducer rocketMqProducer;

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
            List<AccountDTO> accountDTOList = doTransfer(command);
            resultDTO.setSuccess(true);
            resultDTO.setAccountList(accountDTOList);
        } catch (Exception e) {
            log.error("handler transfer tradeNo={} exception:", tradeNo, e);
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "Handler transfer failed";
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);
            throw e;
        } finally {
            sendMessageByMq(tradeNo, resultDTO);
        }
    }

    /**
     * 校验是否存在
     */
    private void verifySettleOrder(String tradeNo) {
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("trade_no as tradeNo");
        queryWrapper.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        SettleOrder settleOrder = settleOrderService.getOne(queryWrapper);
        if (Objects.nonNull(settleOrder)) {
            throw new PaymentException("transfer settle order has exist. " + tradeNo);
        }
    }

    /**
     * 转账操作的资金操作
     */
    private List<AccountDTO> doTransfer(SettleTransferCommand command) {
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
        transferCommand.setMerchantId(transferOutMerchantId);
        transferCommand.setMerchantName(transferOutMerchantName);
        transferCommand.setAccountNo(transferOutMerchantAccountNo);
        transferCommand.setAccountOptType(AccountOptTypeEnum.TRANSFER);
//        transferCommand.setCurrency(accountRecord.getCurrency());
//        transferCommand.setAmount(accountRecord.getMerchantAmount());
        transferCommand.setMerchantFee(BigDecimal.ZERO);
        transferCommand.setMerchantProfit(BigDecimal.ZERO);
        transferCommand.setChannelCost(BigDecimal.ZERO);
        transferCommand.setAccountAmount(BigDecimal.ZERO);
        transferCommand.setPlatformProfit(BigDecimal.ZERO);
//        transferCommand.setTradeNo(accountRecord.getTradeNo());
        transferCommand.setDeductionType(DeductionTypeEnum.DEDUCTION_INTERNAL.getCode()); //默认
        transferCommand.setTransferToMerchantId(transferToMerchantId);
        transferCommand.setTransferToMerchantName(transferToMerchantName);
        transferCommand.setTransferToAccount(transferToAccount);
        List<AccountDTO> accountDTOList = settleAccountCmdService.handlerAccountTransfer(transferCommand);
        log.info("account transfer tradeNo={} result={}", tradeNo, JSONUtil.toJsonStr(accountDTOList));

        //保存结算流水
        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setBusinessNo(command.getBusinessNo());
        settleOrder.setSettleNo(IdWorker.getIdStr());
        settleOrder.setSettleType(SettleTypeEnum.D0.name());  //解冻、出款 都是D0
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
        settleOrder.setActualSettleTime(LocalDateTime.now());
        settleOrder.setCreateTime(LocalDateTime.now());
        settleOrder.setAttribute("{}");
        settleOrderService.save(settleOrder);

        return accountDTOList;
    }

    /**
     * mq发送消息
     */
    private void sendMessageByMq(String tradeNo, SettleResultDTO resultDTO) {
        SettleFinishMessageDTO dto = new SettleFinishMessageDTO();
        dto.setTradeNo(tradeNo);
        dto.setSettleTime(LocalDateTime.now().format(TradeConstant.DF_0));
        dto.setSettleStatus(resultDTO.isSuccess());
        dto.setRemark(resultDTO.getErrorMsg());

        //余额信息
        List<AccountDTO> accountList = resultDTO.getAccountList();
        if (CollectionUtils.isNotEmpty(accountList)) {
            List<SettleFinishAccountMessageDTO> accountMessageDTOList = accountList.stream().map(accountDTO -> {
                SettleFinishAccountMessageDTO accountMessageDTO = new SettleFinishAccountMessageDTO();
                accountMessageDTO.setAccountNo(accountDTO.getAccountNo());
                accountMessageDTO.setAccountName(accountDTO.getAccountName());
                accountMessageDTO.setCurrency(accountDTO.getCurrency());
                accountMessageDTO.setAvailableBalance(accountDTO.getAvailableBalance());
                accountMessageDTO.setFrozenBalance(accountDTO.getFrozenBalance());
                accountMessageDTO.setToSettleBalance(accountDTO.getToSettleBalance());
                return accountMessageDTO;
            }).collect(Collectors.toList());
            dto.setAccountList(accountMessageDTOList);
        }

        SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.SETTLE_FINISH_TOPIC, JSONUtil.toJsonStr(dto));
        log.info("transfer sendMessageByMq result={}", sendResult);
    }

}
