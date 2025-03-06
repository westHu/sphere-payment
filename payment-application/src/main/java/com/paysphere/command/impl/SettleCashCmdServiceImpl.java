package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.paysphere.command.SettleAccountCmdService;
import com.paysphere.command.SettleCashCmdService;
import com.paysphere.command.cmd.SettleAccountUpdateCashCommand;
import com.paysphere.command.cmd.SettleCashMessageCommand;
import com.paysphere.command.dto.AccountDTO;
import com.paysphere.command.dto.OperateDTO;
import com.paysphere.command.dto.SettleAttributeDTO;
import com.paysphere.command.dto.SettleResultDTO;
import com.paysphere.db.entity.SettleOrder;
import com.paysphere.enums.AccountOptTypeEnum;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.PaymentException;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.mq.dto.SettleFinishAccountMessageDTO;
import com.paysphere.mq.dto.SettleFinishMessageDTO;
import com.paysphere.repository.SettleOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.paysphere.TradeConstant.DF_0;
import static com.paysphere.TradeConstant.DF_1;
import static com.paysphere.TradeConstant.LIMIT_1;
import static com.paysphere.TradeConstant.SETTLE_FINISH_TOPIC;


@Slf4j
@Service
public class SettleCashCmdServiceImpl implements SettleCashCmdService {

    @Resource
    SettleOrderService settleOrderService;

    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 逐条结算
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerSettleImmediate(SettleCashMessageCommand command) {
        log.info("handlerCashSettle command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();

        //校验是否存在订单， 此刻应该不存在
        QueryWrapper<SettleOrder> settlerQuery = new QueryWrapper<>();
        settlerQuery.select("trade_no as tradeNo");
        settlerQuery.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(LIMIT_1);
        SettleOrder order = settleOrderService.getOne(settlerQuery);
        if (Objects.nonNull(order)) {
            throw new PaymentException("handlerImmediateSettle. Order already exist. " + tradeNo);
        }

        SettleResultDTO resultDTO = new SettleResultDTO();
        try {
            //D0 即刻结算
            List<AccountDTO> accountDTOList = doImmediateSettle(command);
            resultDTO.setSuccess(true);
            resultDTO.setAccountList(accountDTOList);
        } catch (Exception e) {
            log.error("handlerImmediateSettle tradeNo={} exception:", tradeNo, e);

            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "handlerImmediateSettle failed";
            addSettleOrder(command, SettleStatusEnum.SETTLE_FAILED, errorMsg);
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);

            throw e;
        } finally {
            sendMessageByMq(tradeNo, resultDTO);
        }
    }

    /**
     * 重新结算
     */
    @Override
    public void handlerReSettle(SettleOrder order, String operator) {
        String tradeNo = order.getTradeNo();


        //实扣金额
        BigDecimal actualAmount = order.getAccountAmount().add(order.getMerchantFee());

        //执行出款操作
        SettleAccountUpdateCashCommand cashCommand = new SettleAccountUpdateCashCommand();
        cashCommand.setMerchantId(order.getMerchantId());
        cashCommand.setMerchantName(order.getMerchantName());
        cashCommand.setAccountNo(order.getAccountNo());
        cashCommand.setAccountOptType(AccountOptTypeEnum.CASH);
        cashCommand.setCurrency(order.getCurrency());
        cashCommand.setAmount(order.getAmount()); //交易金额
        cashCommand.setActualAmount(actualAmount); //实扣金额
        cashCommand.setMerchantFee(order.getMerchantFee());
        cashCommand.setMerchantProfit(order.getMerchantProfit());
        cashCommand.setChannelCost(order.getChannelCost());
        cashCommand.setAccountAmount(order.getAccountAmount());
        cashCommand.setPlatformProfit(order.getPlatformProfit());
        cashCommand.setTradeNo(tradeNo);
        cashCommand.setDeductionType(order.getDeductionType());
        List<AccountDTO> accountDTOList = settleAccountCmdService.handlerAccountCash(cashCommand);
        log.info("ReSettle account cash CASH result={}", JSONUtil.toJsonStr(accountDTOList));

        OperateDTO operateDTO = new OperateDTO();
        operateDTO.setOperateType("reSettlement");
        operateDTO.setOperator(operator);
        operateDTO.setOperatorTime(LocalDateTime.now().format(DF_1));

        updateSettleOrder(order, operateDTO);

        //发送消息
        SettleResultDTO resultDTO = new SettleResultDTO();
        resultDTO.setSuccess(true);
        resultDTO.setAccountList(accountDTOList);
        sendMessageByMq(tradeNo, resultDTO);
    }

    /**
     * 即刻结算
     */
    private List<AccountDTO> doImmediateSettle(SettleCashMessageCommand command) {
        log.info("doImmediateSettle command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();


        //执行出款操作
        SettleAccountUpdateCashCommand cashCommand = new SettleAccountUpdateCashCommand();
        cashCommand.setMerchantId(command.getMerchantId());
        cashCommand.setMerchantName(command.getMerchantName());
        cashCommand.setAccountNo(command.getAccountNo());
        cashCommand.setAccountOptType(AccountOptTypeEnum.CASH);
        cashCommand.setCurrency(command.getCurrency());
        cashCommand.setAmount(command.getAmount()); //交易金额
        cashCommand.setActualAmount(command.getActualAmount()); //实扣金额
        cashCommand.setMerchantFee(command.getMerchantFee());
        cashCommand.setMerchantProfit(command.getMerchantProfit());
        cashCommand.setChannelCost(command.getChannelCost());
        cashCommand.setAccountAmount(command.getAccountAmount());
        cashCommand.setPlatformProfit(command.getPlatformProfit());
        cashCommand.setTradeNo(tradeNo);
        cashCommand.setOuterNo(command.getOuterNo());
        cashCommand.setDeductionType(command.getDeductionType());
        List<AccountDTO> accountDTOList = settleAccountCmdService.handlerAccountCash(cashCommand);
        log.info("doImmediateSettle account cash result={}", JSONUtil.toJsonStr(accountDTOList));

        //新增'成功'结算订单
        addSettleOrder(command, SettleStatusEnum.SETTLE_SUCCESS, null);
        return accountDTOList;
    }

    /**
     * 更新结算订单
     */
    private void updateSettleOrder(SettleOrder order, OperateDTO operateDTO) {
        UpdateWrapper<SettleOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(SettleOrder::getActualSettleTime, LocalDateTime.now())
                .set(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_SUCCESS.getCode());
        if (Objects.nonNull(operateDTO)) {
            updateWrapper.lambda().setSql("attribute = JSON_SET(attribute, " +
                    "'$." + operateDTO.getOperateType() + "', '" + JSONUtil.toJsonStr(operateDTO) + "') ");
        }
        updateWrapper.lambda().eq(SettleOrder::getId, order.getId());
        settleOrderService.update(updateWrapper);
    }

    /**
     * 新增结算订单
     */
    private boolean addSettleOrder(SettleCashMessageCommand command, SettleStatusEnum settleStatusEnum, String msg) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime paymentFinishTime = Optional.of(command).map(SettleCashMessageCommand::getPaymentFinishTime)
                .map(e -> LocalDateTime.parse(e, DF_0))
                .orElse(now);

        SettleAttributeDTO attributeDTO = new SettleAttributeDTO();
        attributeDTO.setError(msg);

        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setBusinessNo(command.getBusinessNo());
        settleOrder.setSettleNo(IdWorker.getIdStr());
        settleOrder.setSettleType(command.getSettleType()); //代付 默认D0
        settleOrder.setTradeNo(command.getTradeNo());
        settleOrder.setTradeType(TradeTypeEnum.PAYOUT.getCode());
        settleOrder.setTradeTime(LocalDateTime.parse(command.getTradeTime(), DF_0));
        settleOrder.setPaymentFinishTime(paymentFinishTime);
        settleOrder.setSettleStatus(settleStatusEnum.getCode());
        settleOrder.setMerchantId(command.getMerchantId());
        settleOrder.setMerchantName(command.getMerchantName());
        settleOrder.setAccountNo(command.getAccountNo());
        settleOrder.setChannelCode(command.getChannelCode());
        settleOrder.setChannelName(command.getChannelName());
        settleOrder.setPaymentMethod(command.getPaymentMethod());
        settleOrder.setDeductionType(command.getDeductionType());
        settleOrder.setCurrency(command.getCurrency());
        settleOrder.setAmount(command.getActualAmount()); //实扣金额
        settleOrder.setMerchantFee(command.getMerchantFee());
        settleOrder.setMerchantProfit(command.getMerchantProfit());
        settleOrder.setChannelCost(command.getChannelCost());
        settleOrder.setPlatformProfit(command.getPlatformProfit());
        settleOrder.setAccountAmount(command.getAccountAmount());
        settleOrder.setActualSettleTime(now); //实际结算时间即是当前
        settleOrder.setOuterNo(command.getOuterNo());
        settleOrder.setAttribute(JSONUtil.toJsonStr(attributeDTO));
        settleOrder.setCreateTime(now);
        return settleOrderService.save(settleOrder);
    }

    /**
     * mq发送批量消息
     */
    private void sendMessageByMq(String tradeNo, SettleResultDTO resultDTO) {
        SettleFinishMessageDTO dto = new SettleFinishMessageDTO();
        dto.setTradeNo(tradeNo);
        dto.setSettleTime(LocalDateTime.now().toString());
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


        SendResult sendResult = rocketMqProducer.syncSend(SETTLE_FINISH_TOPIC, JSONUtil.toJsonStr(dto));
        log.info("cash settle finish send back trade result={}", sendResult);
    }
}


