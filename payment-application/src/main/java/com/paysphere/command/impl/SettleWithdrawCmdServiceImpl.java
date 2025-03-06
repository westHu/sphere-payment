package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.SettleAccountCmdService;
import com.paysphere.command.SettleWithdrawCmdService;
import com.paysphere.command.cmd.SettleAccountWithdrawCommand;
import com.paysphere.command.cmd.SettleWithdrawCommand;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SettleWithdrawCmdServiceImpl implements SettleWithdrawCmdService {

    @Resource
    RedisService redisService;
    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettleOrderService settleOrderService;
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 处理提现操作的资金处理
     */
    @Override
    public void handlerWithdraw(SettleWithdrawCommand command) {
        log.info("handlerWithdraw command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();

        //校验是否存在, 应该不存在, 否则异常
        verifySettleOrder(command.getTradeNo());

        SettleResultDTO resultDTO = new SettleResultDTO();
        try {
            List<AccountDTO> accountDTOList = doWithdraw(command);
            resultDTO.setSuccess(true);
            resultDTO.setAccountList(accountDTOList);
        } catch (Exception e) {
            log.error("handler withdraw tradeNo={} exception:", tradeNo, e);
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "Handler withdraw failed";
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);
            throw e;
        } finally {
            sendMessageByMq(tradeNo, resultDTO);
        }
    }


    //-----------------------

    /**
     * 校验是否存在
     */
    private void verifySettleOrder(String tradeNo) {
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("trade_no as tradeNo");
        queryWrapper.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        SettleOrder settleOrder = settleOrderService.getOne(queryWrapper);
        if (Objects.nonNull(settleOrder)) {
            throw new PaymentException("Withdraw settle order has exist. " + tradeNo);
        }
    }

    /**
     * 提现操作的资金操作
     */
    private List<AccountDTO> doWithdraw(SettleWithdrawCommand command) {
        log.info("doWithdraw command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();
        BigDecimal amount = command.getAmount(); //实扣金额
        BigDecimal merchantFee = command.getMerchantFee(); //商户手续费
        BigDecimal accountAmount = command.getAccountAmount(); //到账金额
        BigDecimal channelCost = command.getChannelCost(); //通道成本
        BigDecimal merchantProfit = BigDecimal.ZERO; //提现没有代理商分润 先保留
        BigDecimal platformProfit = command.getPlatformProfit(); //计算平台利润,

        //执行资金提现操作
        SettleAccountWithdrawCommand withdrawCommand = new SettleAccountWithdrawCommand();
        withdrawCommand.setTradeNo(tradeNo);
        withdrawCommand.setMerchantId(command.getMerchantId());
        withdrawCommand.setMerchantName(command.getMerchantName());
        withdrawCommand.setAccountNo(command.getAccountNo());

        withdrawCommand.setCurrency(command.getCurrency());
        withdrawCommand.setAmount(amount); //交易金额 = 实扣金额
        withdrawCommand.setMerchantFee(merchantFee); //商户手续费 (线下打款=0)
        withdrawCommand.setAccountAmount(accountAmount); //商户到账金额
        withdrawCommand.setChannelCost(channelCost); //通道的成本
        withdrawCommand.setPlatformProfit(platformProfit); //通道的成本
        List<AccountDTO> accountDTOList = settleAccountCmdService.handlerAccountWithdraw(withdrawCommand);
        log.info("account doWithdraw result={}", JSONUtil.toJsonStr(accountDTOList));

        //保存结算流水
        LocalDateTime tradeTime = Optional.of(command).map(SettleWithdrawCommand::getTradeTime)
                .map(t -> LocalDateTime.parse(t, TradeConstant.DF_0))
                .orElse(LocalDateTime.now());

        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setBusinessNo(command.getBusinessNo());
        settleOrder.setSettleNo(IdWorker.getIdStr());
        settleOrder.setSettleType(SettleTypeEnum.D0.name());
        settleOrder.setSettleTime(LocalTime.now().format(TradeConstant.DF_1));
        settleOrder.setTradeNo(tradeNo);
        settleOrder.setTradeType(TradeTypeEnum.WITHDRAW.getCode());
        settleOrder.setTradeTime(tradeTime);
        settleOrder.setPaymentFinishTime(tradeTime);
        settleOrder.setSettleStatus(SettleStatusEnum.SETTLE_SUCCESS.getCode());
        settleOrder.setMerchantId(command.getMerchantId());
        settleOrder.setMerchantName(command.getMerchantName());
        settleOrder.setAccountNo(command.getAccountNo());
        settleOrder.setChannelCode(null);
        settleOrder.setChannelName(null);
        settleOrder.setPaymentMethod(null);
        settleOrder.setDeductionType(DeductionTypeEnum.DEDUCTION_INTERNAL.getCode()); //暂时固定
        settleOrder.setCurrency(command.getCurrency());
        settleOrder.setAmount(command.getAmount());
        settleOrder.setMerchantFee(command.getMerchantFee());
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
        log.info("withdraw sendMessageByMq result={}", sendResult);
    }

}
