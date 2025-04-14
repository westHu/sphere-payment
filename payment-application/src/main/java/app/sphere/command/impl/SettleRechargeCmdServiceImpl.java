package app.sphere.command.impl;

import app.sphere.command.dto.SettleResultDTO;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import share.sphere.TradeConstant;
import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.SettleRechargeCmdService;
import app.sphere.command.cmd.SettleAccountRechargeCommand;
import app.sphere.command.cmd.SettleRechargeCommand;
import app.sphere.command.dto.AccountDTO;
import infrastructure.sphere.db.entity.SettleOrder;
import share.sphere.enums.DeductionTypeEnum;
import share.sphere.enums.SettleStatusEnum;
import share.sphere.enums.SettleTypeEnum;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.PaymentException;
import domain.sphere.repository.SettleOrderRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class SettleRechargeCmdServiceImpl implements SettleRechargeCmdService {

    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettleOrderRepository settleOrderRepository;

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
            doRecharge(command);
            resultDTO.setSuccess(true);
        } catch (Exception e) {
            log.error("handlerRecharge tradeNo={} exception:", tradeNo, e);

            //FIX 新增充值失败结算记录
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "handlerRecharge failed";
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);

            throw e;
        } finally {
            notifySettleResult(tradeNo, resultDTO);
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
            throw new PaymentException("Recharge settle order has exist. " + tradeNo);
        }
    }

    /**
     * 充值操作的资金操作
     */
    private void doRecharge(SettleRechargeCommand command) {
        log.info("doRecharge command={}", JSONUtil.toJsonStr(command));

        //执行资金充值操作
        SettleAccountRechargeCommand rechargeCommand = new SettleAccountRechargeCommand();
        rechargeCommand.setTradeNo(command.getTradeNo());
        rechargeCommand.setMerchantId(command.getMerchantId());
        rechargeCommand.setMerchantName(command.getMerchantName());
        rechargeCommand.setAccountNo(command.getAccountNo());
        rechargeCommand.setCurrency(command.getCurrency());
        rechargeCommand.setAmount(command.getAmount());
        settleAccountCmdService.handlerAccountRecharge(rechargeCommand);

        //保存结算订单
        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setSettleType(SettleTypeEnum.T0.name());
        settleOrder.setSettleTime(null);
        settleOrder.setTradeNo(command.getTradeNo());
        settleOrder.setTradeType(TradeTypeEnum.RECHARGE.getCode());
        settleOrder.setTradeTime(command.getTradeTime());
        settleOrder.setPaymentFinishTime(settleOrder.getTradeTime());
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
        settleOrder.setActualSettleTime(System.currentTimeMillis());
        settleOrder.setCreateTime(LocalDateTime.now());
        settleOrder.setAttribute("{}");
        settleOrderRepository.save(settleOrder);
    }

    /**
     * mq发送消息
     */
    private void notifySettleResult(String tradeNo, SettleResultDTO resultDTO) {

    }
}
