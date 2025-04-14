package app.sphere.command.impl;

import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.SettleWithdrawCmdService;
import app.sphere.command.cmd.SettleAccountWithdrawCommand;
import app.sphere.command.cmd.SettleWithdrawCommand;
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
public class SettleWithdrawCmdServiceImpl implements SettleWithdrawCmdService {

    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettleOrderRepository settleOrderRepository;


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
            doWithdraw(command);
            resultDTO.setSuccess(true);
        } catch (Exception e) {
            log.error("handler withdraw tradeNo={} exception:", tradeNo, e);
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "Handler withdraw failed";
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);
            throw e;
        } finally {
            notifySettleResult(tradeNo, resultDTO);
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
        SettleOrder settleOrder = settleOrderRepository.getOne(queryWrapper);
        if (Objects.nonNull(settleOrder)) {
            throw new PaymentException("Withdraw settle order has exist. " + tradeNo);
        }
    }

    /**
     * 提现操作的资金操作
     */
    private void doWithdraw(SettleWithdrawCommand command) {
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
        settleAccountCmdService.handlerAccountWithdraw(withdrawCommand);

        //保存结算流水
        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setSettleType(SettleTypeEnum.T0.name());
        settleOrder.setSettleTime(LocalTime.now().format(TradeConstant.DF_1));
        settleOrder.setTradeNo(tradeNo);
        settleOrder.setTradeType(TradeTypeEnum.WITHDRAW.getCode());
        settleOrder.setTradeTime(System.currentTimeMillis());
        settleOrder.setPaymentFinishTime(System.currentTimeMillis());
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
