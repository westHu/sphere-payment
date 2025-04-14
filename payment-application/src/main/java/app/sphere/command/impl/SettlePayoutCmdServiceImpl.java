package app.sphere.command.impl;

import app.sphere.command.dto.SettleResultDTO;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.SettlePayoutCmdService;
import app.sphere.command.cmd.SettleAccountUpdateCashCommand;
import app.sphere.command.cmd.SettlePayoutCommand;
import app.sphere.command.dto.SettleAttributeDTO;
import infrastructure.sphere.db.entity.SettleOrder;
import share.sphere.enums.AccountOptTypeEnum;
import share.sphere.enums.SettleStatusEnum;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.PaymentException;
import domain.sphere.repository.SettleOrderRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static share.sphere.TradeConstant.LIMIT_1;


@Slf4j
@Service
public class SettlePayoutCmdServiceImpl implements SettlePayoutCmdService {

    @Resource
    SettleOrderRepository settleOrderRepository;
    @Resource
    SettleAccountCmdService settleAccountCmdService;

    /**
     * 逐条结算
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerSettleImmediate(SettlePayoutCommand command) {
        log.info("handlerCashSettle command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();

        //校验是否存在订单， 此刻应该不存在
        QueryWrapper<SettleOrder> settlerQuery = new QueryWrapper<>();
        settlerQuery.select("trade_no as tradeNo");
        settlerQuery.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(LIMIT_1);
        SettleOrder order = settleOrderRepository.getOne(settlerQuery);
        if (Objects.nonNull(order)) {
            throw new PaymentException("handlerImmediateSettle. Order already exist. " + tradeNo);
        }

        SettleResultDTO resultDTO = new SettleResultDTO();
        try {
            doImmediateSettle(command);
            resultDTO.setSuccess(true);
        } catch (Exception e) {
            log.error("handlerImmediateSettle tradeNo={} exception:", tradeNo, e);

            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "handlerImmediateSettle failed";
            addSettleOrder(command, SettleStatusEnum.SETTLE_FAILED, errorMsg);
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);

            throw e;
        } finally {
            notifySettleResult(tradeNo, resultDTO);
        }
    }

    /**
     * 即刻结算
     */
    private void doImmediateSettle(SettlePayoutCommand command) {
        log.info("doImmediateSettle command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();

        //执行出款操作
        SettleAccountUpdateCashCommand cashCommand = new SettleAccountUpdateCashCommand();
        cashCommand.setMerchantId(command.getMerchantId());
        cashCommand.setMerchantName(command.getMerchantName());
        cashCommand.setAccountNo(command.getAccountNo());
        cashCommand.setAccountOptType(AccountOptTypeEnum.PAYOUT);
        cashCommand.setCurrency(command.getCurrency());
        cashCommand.setAmount(command.getAmount()); //交易金额
        cashCommand.setActualAmount(command.getActualAmount()); //实扣金额
        cashCommand.setMerchantFee(command.getMerchantFee());
        cashCommand.setMerchantProfit(command.getMerchantProfit());
        cashCommand.setChannelCost(command.getChannelCost());
        cashCommand.setAccountAmount(command.getAccountAmount());
        cashCommand.setPlatformProfit(command.getPlatformProfit());
        cashCommand.setTradeNo(tradeNo);
        cashCommand.setDeductionType(command.getDeductionType());
        settleAccountCmdService.handlerAccountPayout(cashCommand);

        //新增'成功'结算订单
        addSettleOrder(command, SettleStatusEnum.SETTLE_SUCCESS, null);
    }

    /**
     * 新增结算订单
     */
    private void addSettleOrder(SettlePayoutCommand command, SettleStatusEnum settleStatusEnum, String msg) {
        SettleAttributeDTO attributeDTO = new SettleAttributeDTO();
        attributeDTO.setError(msg);

        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setSettleType(command.getSettleType()); //代付 默认D0
        settleOrder.setTradeNo(command.getTradeNo());
        settleOrder.setTradeType(TradeTypeEnum.PAYOUT.getCode());
        settleOrder.setTradeTime(System.currentTimeMillis());
        settleOrder.setPaymentFinishTime(System.currentTimeMillis());
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
        settleOrder.setActualSettleTime(System.currentTimeMillis()); //实际结算时间即是当前
        settleOrder.setAttribute(JSONUtil.toJsonStr(attributeDTO));
        settleOrder.setCreateTime(LocalDateTime.now());
        settleOrderRepository.save(settleOrder);
    }

    /**
     * 更新Trade
     */
    private void notifySettleResult(String tradeNo, SettleResultDTO resultDTO) {

    }
}


