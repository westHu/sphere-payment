package app.sphere.command.impl;

import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.TradeWithdrawOrderCmdService;
import app.sphere.command.cmd.SettleAccountUpdateFrozenCmd;
import app.sphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import app.sphere.command.cmd.SettleAccountWithdrawCommand;
import app.sphere.command.cmd.TradeWithdrawCommand;
import app.sphere.command.cmd.TradeWithdrawReviewCommand;
import app.sphere.command.dto.trade.result.ReviewResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import app.sphere.manager.FeeManager;
import app.sphere.manager.OrderNoManager;
import app.sphere.query.MerchantQueryService;
import app.sphere.query.SettleAccountQueryService;
import app.sphere.query.dto.MerchantTradeDTO;
import app.sphere.query.param.MerchantTradeParam;
import app.sphere.query.param.SettleAccountParam;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.TradeWithdrawOrderRepository;
import infrastructure.sphere.db.entity.MerchantWithdrawChannelConfig;
import infrastructure.sphere.db.entity.MerchantWithdrawConfig;
import infrastructure.sphere.db.entity.SettleAccount;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;
import infrastructure.sphere.remote.admin.TradeExamineParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.DeductionTypeEnum;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.enums.SettleStatusEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


@Slf4j
@Service
public class TradeWithdrawOrderCmdServiceImpl implements TradeWithdrawOrderCmdService {

    @Resource
    TradeWithdrawOrderRepository tradeWithdrawOrderRepository;
    @Resource
    OrderNoManager orderNoManager;
    @Resource
    MerchantQueryService merchantQueryService;
    @Resource
    SettleAccountQueryService settleAccountQueryService;
    @Resource
    FeeManager feeManager;
    @Resource
    SettleAccountCmdService settleAccountCmdService;

    @Override
    public boolean executeWithdraw(TradeWithdrawCommand command) {
        log.info("executeWithdraw command={}", JSONUtil.toJsonStr(command));
        return doWithdraw(command);
    }


    @Override
    public void executeWithdrawReview(TradeWithdrawReviewCommand command) {
        log.info("executeWithdrawReview command={}", JSONUtil.toJsonStr(command));
        doWithdrawReview(command);
    }


    // ------------------------------------------------------------------------------------------------------

    /**
     * 提现操作
     */
    private boolean doWithdraw(TradeWithdrawCommand command) {
        //校验账户
        SettleAccountParam settleAccountParam = new SettleAccountParam();
        settleAccountParam.setMerchantId(command.getMerchantId());
        settleAccountParam.setAccountNo(command.getAccountNo());
        SettleAccount settleAccount = settleAccountQueryService.getSettleAccount(settleAccountParam);

        // 校验商户
        MerchantTradeParam merchantTradeParam = new MerchantTradeParam();
        merchantTradeParam.setMerchantId(command.getMerchantId());
        merchantTradeParam.setTradeTypeEnum(TradeTypeEnum.PAYOUT);
        merchantTradeParam.setPaymentMethod(command.getPaymentMethod());
        merchantTradeParam.setAmount(command.getAmount());
        merchantTradeParam.setRegion(settleAccount.getRegion());
        MerchantTradeDTO merchantTradeDTO = merchantQueryService.getMerchantTradeDTO(merchantTradeParam);

        // 校验订单 保存订单 - [1]
        TradeWithdrawOrder order = saveTradeWithdrawOrder(command, settleAccount, merchantTradeDTO);
        String tradeNo = order.getTradeNo();

        // 执行冻结操作, 同步
        SettleAccountUpdateFrozenCmd frozenCmd = new SettleAccountUpdateFrozenCmd();
        frozenCmd.setTradeNo(order.getTradeNo());
        frozenCmd.setMerchantId(order.getMerchantId());
        frozenCmd.setMerchantName(order.getMerchantName());
        frozenCmd.setAccountNo(order.getAccountNo());
        frozenCmd.setCurrency(order.getCurrency());
        frozenCmd.setAmount(order.getActualAmount());
        settleAccountCmdService.handlerAccountFrozen(frozenCmd);

        // 发起审核, Mq给管理台, 状态更为审核中
        handleReview(order, command.getApplyOperator());

        // 更新审核中
        UpdateWrapper<TradeWithdrawOrder> withdrawOrderUpdate = new UpdateWrapper<>();
        withdrawOrderUpdate.lambda()
                .set(TradeWithdrawOrder::getTradeStatus, TradeStatusEnum.TRADE_REVIEW.getCode())
                .eq(TradeWithdrawOrder::getTradeNo, tradeNo);
        return tradeWithdrawOrderRepository.update(withdrawOrderUpdate);
    }


    /**
     * 校验订单 保存订单
     */
    private TradeWithdrawOrder saveTradeWithdrawOrder(TradeWithdrawCommand command,
                                                      SettleAccount settleAccount,
                                                      MerchantTradeDTO merchantTradeDTO) {
        String area = settleAccount.getRegion();
        MerchantWithdrawConfig merchantWithdrawConfig = merchantTradeDTO.getMerchantWithdrawConfig();
        MerchantWithdrawChannelConfig merchantWithdrawChannelConfig = merchantTradeDTO.getMerchantWithdrawChannelConfig();

        BigDecimal singleRate = merchantWithdrawChannelConfig.getSingleRate();
        BigDecimal singleFee = merchantWithdrawChannelConfig.getSingleFee();
        Integer deductionType = merchantWithdrawConfig.getDeductionType();

        BigDecimal amount = command.getAmount();
        BigDecimal merchantFee = feeManager.calculateMerchantFee(amount, singleRate, singleFee);
        DeductionTypeEnum deductionTypeEnum = DeductionTypeEnum.codeToEnum(deductionType);
        BigDecimal actualAmount;
        BigDecimal accountAmount;
        if (DeductionTypeEnum.DEDUCTION_INTERNAL.equals(deductionTypeEnum)) {
            actualAmount = amount;
            accountAmount = amount.subtract(merchantFee);
        } else if (DeductionTypeEnum.DEDUCTION_EXTERNAL.equals(deductionTypeEnum)) {
            actualAmount = amount.add(merchantFee);
            accountAmount = amount;
        } else {
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR);
        }

        // 到账金额，可能会因为内扣导致到账金额小于0
        if (accountAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("saveTradeWithdrawOrder accountAmount is less than zero.");
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR);
        }
        String tradeNo = orderNoManager.getTradeNo(area, TradeTypeEnum.WITHDRAW, command.getMerchantId());

        TradeWithdrawOrder order = new TradeWithdrawOrder();
        order.setTradeNo(tradeNo);
        order.setPurpose(command.getPurpose());
        order.setMerchantId(command.getMerchantId());
        order.setMerchantName(command.getMerchantName());
        order.setAccountNo(command.getAccountNo());

        // 金额 实扣金额 商户手续费 商户分润 到账金额 通道成本 平台利润
        order.setCurrency(command.getCurrency());
        order.setAmount(command.getAmount());
        order.setActualAmount(actualAmount);
        order.setMerchantProfit(BigDecimal.ZERO);
        order.setMerchantFee(BigDecimal.ZERO);
        order.setAccountAmount(accountAmount);

        BigDecimal exchangeRate = BigDecimal.ZERO;
    //    String targetCurrency = AreaEnum.codeToEnum(area).getCurrency().name();
//        order.setWithdrawTargetCurrency(targetCurrency);
        order.setWithdrawTargetAmount(accountAmount.multiply(exchangeRate));
        order.setExchangeRate(exchangeRate);

        order.setPaymentMethod(command.getPaymentMethod());
        order.setBankAccount(command.getBankAccount());
        order.setBankAccountName(command.getBankAccountName());
        order.setTradeTime(System.currentTimeMillis());
        order.setTradeStatus(TradeStatusEnum.TRADE_INIT.getCode());
        order.setPaymentStatus(PaymentStatusEnum.PAYMENT_PENDING.getCode());
        order.setSettleStatus(SettleStatusEnum.SETTLE_TODO.getCode());
        order.setCreateTime(LocalDateTime.now());
        tradeWithdrawOrderRepository.save(order);
        return order;
    }


    /**
     * 发起审核
     */
    private void handleReview(TradeWithdrawOrder order, String applyOperator) {
        String tradeNo = order.getTradeNo();

        TradeExamineParam examineParam = new TradeExamineParam();
        examineParam.setTradeNo(tradeNo);
        examineParam.setMerchantId(order.getMerchantId());
        examineParam.setMerchantName(order.getMerchantName());
        examineParam.setProductDetail(order.getPurpose());
        examineParam.setItemDetailInfo(order.getPurpose());
        examineParam.setCurrency(order.getCurrency());
        examineParam.setAmount(order.getAmount().toString());
        examineParam.setPaymentMethod(order.getPaymentMethod());
        examineParam.setCashAccount(order.getBankAccount());
        examineParam.setApplyOperator(applyOperator);
    }

    /**
     * 提现审核
     */
    private boolean doWithdrawReview(TradeWithdrawReviewCommand command) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradeWithdrawOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeWithdrawOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeWithdrawOrder order = tradeWithdrawOrderRepository.getOne(orderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo));

        // 判断是否在审核中状态
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!TradeStatusEnum.TRADE_REVIEW.equals(tradeStatusEnum)) {
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo);
        }

        // 先解析TradeResult 构建审核结果
        TradeResultDTO tradeResultDTO = Optional.of(order)
                .map(TradeWithdrawOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());
        ReviewResultDTO reviewResultDTO = new ReviewResultDTO();
        reviewResultDTO.setReviewStatus(command.isReviewStatus());
        reviewResultDTO.setReviewTime(System.currentTimeMillis());
        reviewResultDTO.setReviewMsg(command.getReviewMsg());
        tradeResultDTO.setReviewResult(reviewResultDTO);

        // 审核驳回
        if (!command.isReviewStatus()) {
            // 设置失败
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(command.getReviewMsg());

            // 解冻资金
            SettleAccountUpdateUnFrozenCmd unFrozenCmd = new SettleAccountUpdateUnFrozenCmd();
            unFrozenCmd.setTradeNo(order.getTradeNo());
            settleAccountCmdService.handlerAccountUnFrozen(unFrozenCmd);

            // 更新订单状态
            UpdateWrapper<TradeWithdrawOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(TradeWithdrawOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeWithdrawOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .set(TradeWithdrawOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_FAILED.getCode())
                    .set(TradeWithdrawOrder::getPaymentFinishTime, LocalDateTime.now())
                    .eq(TradeWithdrawOrder::getId, order.getId());
            return tradeWithdrawOrderRepository.update(updateWrapper);
        }

        // 审核通过 处理转账资金操作, FIX 比较依赖中间件MQ的稳定性
        SettleAccountWithdrawCommand withdrawCommand = new SettleAccountWithdrawCommand();
        BeanUtils.copyProperties(order, withdrawCommand);
        settleAccountCmdService.handlerAccountWithdraw(withdrawCommand);

        // 审核通过 处理提现订单
        tradeResultDTO.setSuccess(true);
        UpdateWrapper<TradeWithdrawOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(TradeWithdrawOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(TradeWithdrawOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .set(TradeWithdrawOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_SUCCESS.getCode())
                .set(TradeWithdrawOrder::getPaymentFinishTime, LocalDateTime.now())
                .eq(TradeWithdrawOrder::getId, order.getId());
        tradeWithdrawOrderRepository.update(updateWrapper);

        return true;
    }

}
