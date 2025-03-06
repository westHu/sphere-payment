package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.SettleAccountCmdService;
import com.paysphere.command.SettleRechargeCmdService;
import com.paysphere.command.cmd.SettleAccountRechargeCommand;
import com.paysphere.command.cmd.SettleRechargeCommand;
import com.paysphere.command.dto.AccountDTO;
import com.paysphere.command.dto.SettleResultDTO;
import com.paysphere.db.entity.SettleOrder;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SettleRechargeCmdServiceImpl implements SettleRechargeCmdService {

    @Resource
    RedisService redisService;
    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettleOrderService settleOrderService;
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 处理充值操作的资金处理
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handlerRecharge(SettleRechargeCommand command) {
        log.info("handlerRecharge command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();

        //校验是否存在, 应该不存在, 否则异常
        verifySettleOrder(tradeNo);

        SettleResultDTO resultDTO = new SettleResultDTO();
        try {
            List<AccountDTO> accountDTOList = doRecharge(command);
            resultDTO.setSuccess(true);
            resultDTO.setAccountList(accountDTOList);
        } catch (Exception e) {
            log.error("handlerRecharge tradeNo={} exception:", tradeNo, e);

            //FIX 新增充值失败结算记录
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "handlerRecharge failed";
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
            throw new PaymentException("Recharge settle order has exist. " + tradeNo);
        }
    }

    /**
     * 充值操作的资金操作
     */
    private List<AccountDTO> doRecharge(SettleRechargeCommand command) {
        log.info("doRecharge command={}", JSONUtil.toJsonStr(command));

        //执行资金充值操作
        SettleAccountRechargeCommand rechargeCommand = new SettleAccountRechargeCommand();
        rechargeCommand.setTradeNo(command.getTradeNo());
        rechargeCommand.setMerchantId(command.getMerchantId());
        rechargeCommand.setMerchantName(command.getMerchantName());
        rechargeCommand.setAccountNo(command.getAccountNo());
        rechargeCommand.setCurrency(command.getCurrency());
        rechargeCommand.setAmount(command.getAmount());
        List<AccountDTO> accountDTOList = settleAccountCmdService.handlerAccountRecharge(rechargeCommand);
        log.info("account doRecharge result={}", JSONUtil.toJsonStr(accountDTOList));

        //保存结算流水
        LocalDateTime tradeTime = Optional.of(command).map(SettleRechargeCommand::getTradeTime)
                .map(t -> LocalDateTime.parse(t, TradeConstant.DF_0))
                .orElse(LocalDateTime.now());

        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setBusinessNo(command.getBusinessNo());
        settleOrder.setSettleNo(IdWorker.getIdStr());
        settleOrder.setSettleType(SettleTypeEnum.D0.name());
        settleOrder.setSettleTime(LocalTime.now().format(TradeConstant.DF_1));
        settleOrder.setTradeNo(command.getTradeNo());
        settleOrder.setTradeType(TradeTypeEnum.RECHARGE.getCode());
        settleOrder.setTradeTime(tradeTime);
        settleOrder.setPaymentFinishTime(tradeTime);
        settleOrder.setSettleStatus(SettleStatusEnum.SETTLE_SUCCESS.getCode());
        settleOrder.setMerchantId(command.getMerchantId());
        settleOrder.setMerchantName(command.getMerchantName());
        settleOrder.setAccountNo(command.getAccountNo());
        settleOrder.setChannelCode(null);
        settleOrder.setChannelName(null);
        settleOrder.setPaymentMethod(command.getPaymentMethod());

        settleOrder.setDeductionType(DeductionTypeEnum.DEDUCTION_INTERNAL.getCode());
        settleOrder.setCurrency(command.getCurrency());
        settleOrder.setAmount(command.getAmount());
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
        log.info("recharge sendMessageByMq result={}", sendResult);
    }
}
