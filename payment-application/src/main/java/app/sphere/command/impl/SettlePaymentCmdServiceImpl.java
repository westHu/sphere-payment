package app.sphere.command.impl;

import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.SettlePaymentCmdService;
import app.sphere.command.cmd.SettleAccountUpdateSettleCommand;
import app.sphere.command.cmd.SettlePaymentCommand;
import app.sphere.command.dto.SettleResultDTO;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.SettleOrderRepository;
import infrastructure.sphere.db.entity.SettleOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.sphere.TradeConstant;
import share.sphere.enums.*;
import share.sphere.exception.PaymentException;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
public class SettlePaymentCmdServiceImpl implements SettlePaymentCmdService {

    @Resource
    SettleOrderRepository settleOrderRepository;
    @Resource
    SettleAccountCmdService settleAccountCmdService;

    /**
     * 即刻结算
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerSettleImmediate(SettlePaymentCommand command) {
        log.info("handlerPaySettle command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();

        SettleResultDTO resultDTO = new SettleResultDTO();
        try {
            doImmediateSettle(command);
            resultDTO.setSuccess(true);
        } catch (Exception e) {
            log.error("handlerImmediateSettle tradeNo={} exception:", tradeNo, e);

            //新增失败的结算单，可进行二次结算
            addSettleOrder(command, SettleStatusEnum.SETTLE_FAILED);
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "handlerImmediateSettle failed";
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);
            throw e; //抛出异常，保持事务
        } finally {
            notifySettleResult(tradeNo, resultDTO);
        }
    }

    /**
     * 延迟结算 按Job结算
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerSettleJob(SettlePaymentCommand command) {
        log.info("handlerPaySettleJob command={}", JSONUtil.toJsonStr(command));

        //查询‘待结算'订单， 应该存在
        QueryWrapper<SettleOrder> settlerQuery = new QueryWrapper<>();
        settlerQuery.lambda().eq(SettleOrder::getTradeNo, command.getTradeNo()).last(TradeConstant.LIMIT_1);
        SettleOrder order = settleOrderRepository.getOne(settlerQuery);
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
            doJobSettle(order);
            resultDTO.setSuccess(true);
        } catch (Exception e) {
            log.error("handlerJobSettle tradeNo={} exception:", tradeNo, e);

            //更新订单为失败，可以二次结算
            updateSettleOrder(order, SettleStatusEnum.SETTLE_FAILED);
            String errorMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "handlerSettleJob failed";
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(errorMsg);
            throw e;
        } finally {
            notifySettleResult(tradeNo, resultDTO);
        }
    }

    /**
     * 新增结算订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSettleOrder(SettlePaymentCommand command, SettleStatusEnum settleStatusEnum) {
        //执行资金操作 待结算则需要增加待结算金额
        Long actualSettleTime = System.currentTimeMillis();
        if (settleStatusEnum.equals(SettleStatusEnum.SETTLE_TODO)) {
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
            updateCommand.setDeductionType(command.getDeductionType());
            settleAccountCmdService.handlerAccountSettlement(updateCommand);
            actualSettleTime = null;
        }

        //新增结算订单
        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setSettleType(command.getSettleType());
        settleOrder.setSettleTime(command.getSettleTime());
        settleOrder.setTradeNo(command.getTradeNo());
        settleOrder.setTradeType(TradeTypeEnum.PAYMENT.getCode());
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
        settleOrder.setAmount(command.getAmount());
        settleOrder.setMerchantFee(command.getMerchantFee());
        settleOrder.setMerchantProfit(command.getMerchantProfit());
        settleOrder.setChannelCost(command.getChannelCost());
        settleOrder.setPlatformProfit(command.getPlatformProfit());
        settleOrder.setAccountAmount(command.getAccountAmount());
        settleOrder.setActualSettleTime(actualSettleTime);
        settleOrder.setCreateTime(LocalDateTime.now());
        settleOrderRepository.save(settleOrder);
    }

    //-----------------------------

    /**
     * 即刻结算
     */
    private void doImmediateSettle(SettlePaymentCommand command) {
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
        updateCommand.setDeductionType(command.getDeductionType());
        settleAccountCmdService.handlerAccountSettlement(updateCommand);

        //新增成功结算订单
        addSettleOrder(command, SettleStatusEnum.SETTLE_SUCCESS);
    }

    /**
     * Job结算
     */
    private void doJobSettle(SettleOrder order) {

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
        updateCommand.setDeductionType(order.getDeductionType());
        settleAccountCmdService.handlerAccountSettlement(updateCommand);

        updateSettleOrder(order, SettleStatusEnum.SETTLE_SUCCESS);
    }

    /**
     * 逐条结算 - 成功更新结算订单 (商户ID、商户账户号维度)
     */
    private void updateSettleOrder(SettleOrder order, SettleStatusEnum settleStatusEnum) {
        UpdateWrapper<SettleOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(SettleOrder::getActualSettleTime, LocalDateTime.now())
                .set(SettleOrder::getSettleStatus, settleStatusEnum.getCode());
        updateWrapper.lambda().eq(SettleOrder::getTradeNo, order.getTradeNo());
        settleOrderRepository.update(updateWrapper);
    }

    /**
     * 更新Trade
     */
    private void notifySettleResult(String tradeNo, SettleResultDTO resultDTO) {

    }

}


