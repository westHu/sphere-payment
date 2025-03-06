package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.paysphere.TradeConstant;
import com.paysphere.command.SettleAccountCmdService;
import com.paysphere.command.SettlePayCmdService;
import com.paysphere.command.cmd.SettleAccountUpdateSettleCommand;
import com.paysphere.command.cmd.SettlePayMessageCommand;
import com.paysphere.command.dto.AccountDTO;
import com.paysphere.command.dto.OperateDTO;
import com.paysphere.command.dto.SettleResultDTO;
import com.paysphere.db.entity.SettleOrder;
import com.paysphere.enums.AccountOptTypeEnum;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.SettleTypeEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.PaymentException;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.mq.dto.SettleFinishAccountMessageDTO;
import com.paysphere.mq.dto.SettleFinishMessageDTO;
import com.paysphere.repository.SettleAccountFlowService;
import com.paysphere.repository.SettleOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SettlePayCmdServiceImpl implements SettlePayCmdService {

    @Resource
    SettleOrderService settleOrderService;
    @Resource
    SettleAccountCmdService settleAccountCmdService;

    @Resource
    SettleAccountFlowService accountFlowService;
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 即刻结算 按订单，逐条结算
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerSettleImmediate(SettlePayMessageCommand command) {
        log.info("handlerPaySettle command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();

        SettleResultDTO resultDTO = new SettleResultDTO();
        try {
            List<AccountDTO> accountDTOList = doImmediateSettle(command);
            resultDTO.setSuccess(true);
            resultDTO.setAccountList(accountDTOList);
        } catch (Exception e) {
            log.error("handlerImmediateSettle tradeNo={} exception:", tradeNo, e);

            //新增失败的结算单，可进行二次结算
            addSettleOrder(command, SettleStatusEnum.SETTLE_FAILED);
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "handlerImmediateSettle failed";
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);
            throw e; //抛出异常，保持事务
        } finally {
            sendMessageByMq(tradeNo, resultDTO);
        }
    }

    /**
     * 延迟结算 按Job结算
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerSettleJob(SettlePayMessageCommand command) {
        log.info("handlerPaySettleJob command={}", JSONUtil.toJsonStr(command));

        //查询‘待结算'订单， 应该存在
        QueryWrapper<SettleOrder> settlerQuery = new QueryWrapper<>();
        settlerQuery.lambda().eq(SettleOrder::getTradeNo, command.getTradeNo()).last(TradeConstant.LIMIT_1);
        SettleOrder order = settleOrderService.getOne(settlerQuery);
        if (Objects.isNull(order)) {
            throw new PaymentException("handlerJobSettle. Order not exist. " + command.getTradeNo());
        }

        //校验订单状态 - 待结算/失败
        if (!SettleStatusEnum.needToSettle().contains(order.getSettleStatus())) {
            throw new PaymentException("handlerJobSettle. Order status error. " + command.getTradeNo());
        }

        String tradeNo = command.getTradeNo();
        SettleResultDTO resultDTO = new SettleResultDTO();
        try {
            List<AccountDTO> accountDTOList  = doJobSettle(order);
            resultDTO.setSuccess(true);
            resultDTO.setAccountList(accountDTOList);
        } catch (Exception e) {
            log.error("handlerJobSettle tradeNo={} exception:", tradeNo, e);

            //更新订单为失败，可以二次结算
            updateSettleOrder(order, SettleStatusEnum.SETTLE_FAILED, null);
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "handlerSettleJob failed";
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);
            throw e;
        } finally {
            sendMessageByMq(tradeNo, resultDTO);
        }
    }

    /**
     * 新增结算订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSettleOrder(SettlePayMessageCommand command, SettleStatusEnum settleStatusEnum) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tradeTime = LocalDateTime.parse(command.getTradeTime(), TradeConstant.DF_0);
        LocalDateTime paymentFinishTime = Optional.of(command).map(SettlePayMessageCommand::getPaymentFinishTime)
                .map(e -> LocalDateTime.parse(e, TradeConstant.DF_0))
                .orElse(now);

        SettleTypeEnum settleTypeEnum = Optional.of(command).map(SettlePayMessageCommand::getSettleType)
                .map(SettleTypeEnum::valueOf)
                .orElse(SettleTypeEnum.D1);
        String settleTime = Optional.of(command).map(SettlePayMessageCommand::getSettleTime).orElse("00:00:00");

        //执行资金操作 待结算则需要增加待结算金额
        LocalDateTime actualSettleTime = now;
        if (settleStatusEnum.equals(SettleStatusEnum.SETTLE_TODO)) {
            actualSettleTime = null;
            SettleAccountUpdateSettleCommand updateCommand = new SettleAccountUpdateSettleCommand();
            updateCommand.setMerchantId(command.getMerchantId());
            updateCommand.setMerchantName(command.getMerchantName());
            updateCommand.setAccountNo(command.getAccountNo());
            updateCommand.setAccountOptType(AccountOptTypeEnum.PRE_SETTLE);
            updateCommand.setCurrency(command.getCurrency());
            updateCommand.setAmount(command.getAmount());
            updateCommand.setMerchantFee(command.getMerchantFee());
            updateCommand.setMerchantProfit(command.getMerchantProfit());
            updateCommand.setChannelCost(command.getChannelCost());
            updateCommand.setAccountAmount(command.getAccountAmount());
            updateCommand.setPlatformProfit(command.getPlatformProfit());
            updateCommand.setTradeNo(command.getTradeNo());
            updateCommand.setOuterNo(command.getOuterNo());
            updateCommand.setDeductionType(command.getDeductionType());
            List<AccountDTO> accountDTOList = settleAccountCmdService.handlerAccountSettlement(updateCommand);
            log.info("addSettleOrder preSettlement={}", JSONUtil.toJsonStr(accountDTOList));
        }

        //新增结算订单
        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setBusinessNo(command.getBusinessNo());
        settleOrder.setSettleNo(IdWorker.getIdStr());
        settleOrder.setSettleType(settleTypeEnum.name());
        settleOrder.setSettleTime(settleTime);
        settleOrder.setTradeNo(command.getTradeNo());
        settleOrder.setTradeType(TradeTypeEnum.PAYMENT.getCode());
        settleOrder.setTradeTime(tradeTime);
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
        settleOrder.setAmount(command.getAmount());
        settleOrder.setMerchantFee(command.getMerchantFee());
        settleOrder.setMerchantProfit(command.getMerchantProfit());
        settleOrder.setChannelCost(command.getChannelCost());
        settleOrder.setPlatformProfit(command.getPlatformProfit());
        settleOrder.setAccountAmount(command.getAccountAmount());
        settleOrder.setActualSettleTime(actualSettleTime);
        settleOrder.setOuterNo(command.getOuterNo());
        settleOrder.setCreateTime(now);
        settleOrderService.save(settleOrder);
    }

    //-----------------------------

    /**
     * 即刻结算
     */
    private List<AccountDTO> doImmediateSettle(SettlePayMessageCommand command) {

        //执行资金操作， 即可计算- 金额不经过带结算直接进入可用余额
        SettleAccountUpdateSettleCommand updateCommand = new SettleAccountUpdateSettleCommand();
        updateCommand.setMerchantId(command.getMerchantId());
        updateCommand.setMerchantName(command.getMerchantName());
        updateCommand.setAccountNo(command.getAccountNo());
        updateCommand.setAccountOptType(AccountOptTypeEnum.IMMEDIATE_SETTLE);
        updateCommand.setCurrency(command.getCurrency());
        updateCommand.setAmount(command.getAmount());
        updateCommand.setMerchantFee(command.getMerchantFee());
        updateCommand.setMerchantProfit(command.getMerchantProfit());
        updateCommand.setChannelCost(command.getChannelCost());
        updateCommand.setAccountAmount(command.getAccountAmount());
        updateCommand.setPlatformProfit(command.getPlatformProfit());
        updateCommand.setTradeNo(command.getTradeNo());
        updateCommand.setOuterNo(command.getOuterNo());
        updateCommand.setDeductionType(command.getDeductionType());
        List<AccountDTO> accountDTOList = settleAccountCmdService.handlerAccountSettlement(updateCommand);
        log.info("doImmediateSettle pay result={}", JSONUtil.toJsonStr(accountDTOList));

        //新增成功结算订单
        addSettleOrder(command, SettleStatusEnum.SETTLE_SUCCESS);
        return accountDTOList;
    }

    /**
     * Job结算
     */
    private List<AccountDTO> doJobSettle(SettleOrder order) {

        //执行资金操作 - 因为已经存到待结算金额中 选择 DELAYED_SETTLE 延迟结算
        SettleAccountUpdateSettleCommand updateCommand = new SettleAccountUpdateSettleCommand();
        updateCommand.setMerchantId(order.getMerchantId());
        updateCommand.setMerchantName(order.getMerchantName());
        updateCommand.setAccountNo(order.getAccountNo());
        updateCommand.setAccountOptType(AccountOptTypeEnum.DELAYED_SETTLE);
        updateCommand.setCurrency(order.getCurrency());
        updateCommand.setAmount(order.getAmount());
        updateCommand.setMerchantFee(order.getMerchantFee());
        updateCommand.setMerchantProfit(order.getMerchantProfit());
        updateCommand.setChannelCost(order.getChannelCost());
        updateCommand.setAccountAmount(order.getAccountAmount());
        updateCommand.setPlatformProfit(order.getPlatformProfit());
        updateCommand.setTradeNo(order.getTradeNo());
        updateCommand.setOuterNo(order.getOuterNo());
        updateCommand.setDeductionType(order.getDeductionType());
        List<AccountDTO> accountDTOList = settleAccountCmdService.handlerAccountSettlement(updateCommand);
        log.info("doJobSettle account pay SETTLEMENT result={}", JSONUtil.toJsonStr(accountDTOList));
        updateSettleOrder(order, SettleStatusEnum.SETTLE_SUCCESS, null);

        return accountDTOList;
    }

    /**
     * 提前结算
     */
    private List<AccountDTO> doAdvanceSettle(SettleOrder order, OperateDTO operateDTO) {

        //执行资金操作
        SettleAccountUpdateSettleCommand updateCommand = new SettleAccountUpdateSettleCommand();
        updateCommand.setMerchantId(order.getMerchantId());
        updateCommand.setMerchantName(order.getMerchantName());
        updateCommand.setAccountNo(order.getAccountNo());
        updateCommand.setAccountOptType(AccountOptTypeEnum.DELAYED_SETTLE);
        updateCommand.setCurrency(order.getCurrency());
        updateCommand.setAmount(order.getAmount());
        updateCommand.setMerchantFee(order.getMerchantFee());
        updateCommand.setMerchantProfit(order.getMerchantProfit());
        updateCommand.setChannelCost(order.getChannelCost());
        updateCommand.setAccountAmount(order.getAccountAmount());
        updateCommand.setPlatformProfit(order.getPlatformProfit());
        updateCommand.setTradeNo(order.getTradeNo());
        updateCommand.setOuterNo(order.getOuterNo());
        updateCommand.setDeductionType(order.getDeductionType());
        List<AccountDTO> accountDTOList = settleAccountCmdService.handlerAccountSettlement(updateCommand);
        log.info("doAdvanceSettle result={}", JSONUtil.toJsonStr(accountDTOList));
        updateSettleOrder(order, SettleStatusEnum.SETTLE_SUCCESS, operateDTO);

        return accountDTOList;
    }


    /**
     * 逐条结算 - 成功更新结算订单 (商户ID、商户账户号维度)
     */
    private boolean updateSettleOrder(SettleOrder order, SettleStatusEnum settleStatusEnum, OperateDTO operateDTO) {
        UpdateWrapper<SettleOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(SettleOrder::getActualSettleTime, LocalDateTime.now())
                .set(SettleOrder::getSettleStatus, settleStatusEnum.getCode());
        if (Objects.nonNull(operateDTO)) {
            updateWrapper.lambda().setSql("attribute = JSON_SET(attribute, " +
                    "'$." + operateDTO.getOperateType() + "', '" + JSONUtil.toJsonStr(operateDTO) + "') ");
        }
        updateWrapper.lambda().eq(SettleOrder::getTradeNo, order.getTradeNo());
        return settleOrderService.update(updateWrapper);
    }


    /**
     * mq发送批量消息
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
        log.info("paySettle sendMessageByMq result={}", sendResult);
    }

}


