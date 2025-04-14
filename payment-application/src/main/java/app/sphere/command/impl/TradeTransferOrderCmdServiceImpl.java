package app.sphere.command.impl;

import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.TradeTransferOrderCmdService;
import app.sphere.command.cmd.SettleAccountUpdateFrozenCmd;
import app.sphere.command.cmd.SettleAccountUpdateTransferCommand;
import app.sphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import app.sphere.command.cmd.TradeTransferCommand;
import app.sphere.command.cmd.TradeTransferReviewCommand;
import app.sphere.command.dto.TradeTransferAttributeDTO;
import app.sphere.command.dto.trade.result.ReviewResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import app.sphere.manager.OrderNoManager;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.TradeTransferOrderRepository;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.TradeTransferOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.SettleStatusEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
public class TradeTransferOrderCmdServiceImpl  implements TradeTransferOrderCmdService {

    @Resource
    TradeTransferOrderRepository tradeTransferOrderRepository;
    @Resource
    OrderNoManager orderNoManager;
    @Resource
    SettleAccountCmdService settleAccountCmdService;

    @Override
    public boolean executeTransfer(TradeTransferCommand command) {
        log.info("executeTransfer command={}", JSONUtil.toJsonStr(command));
        return doTransfer(command);
    }


    @Override
    public void executeTransferReview(TradeTransferReviewCommand command) {
        handlerTransferReview(command);
    }

    // ------------------------------------------------------------------------------------------------------

    /**
     * 转账操作
     */
    private boolean doTransfer(TradeTransferCommand command) {
        // 校验参数
        verifyAccountNoNotEqual(command);

        // 保存订单，转账有转出传入，保存[2]笔订单
        Pair<TradeTransferOrder, TradeTransferOrder> transferOrderPair = saveTradeTransferOrder(command);
        TradeTransferOrder transferFromOrder = transferOrderPair.getLeft();
        String tradeNo = transferFromOrder.getTradeNo();

        String fromMerchantId = command.getFromMerchantId();
        String toMerchantId = command.getToMerchantId();

        // 如果是同一个商户内部转账, 无需审核
        if (fromMerchantId.equals(toMerchantId)) {
            log.info("doTransfer same merchant transfer. merchantId={}", fromMerchantId);
            return transferWithSameMerchant(transferOrderPair);
        }

        log.info("doTransfer diff merchant transfer. out={}. to={}", fromMerchantId, toMerchantId);

        // 同步 冻结资金
        SettleAccountUpdateFrozenCmd frozenCmd = new SettleAccountUpdateFrozenCmd();
        BeanUtils.copyProperties(transferFromOrder, frozenCmd);
        settleAccountCmdService.handlerAccountFrozen(frozenCmd);

        // 发起审核, Mq给管理台, 状态更为审核中
        transferReview();

        // 更新交易订单：交易状态-审核中
        UpdateWrapper<TradeTransferOrder> transferOrderUpdate = new UpdateWrapper<>();
        transferOrderUpdate.lambda()
                .set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_REVIEW.getCode())
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradeTransferOrder::getTradeNo, tradeNo);
        return tradeTransferOrderRepository.update(transferOrderUpdate);
    }

    /**
     * 相关校验
     * 1, 不能自己转自己
     */
    private void verifyAccountNoNotEqual(TradeTransferCommand command) {
        if (command.getFromAccountNo().equals(command.getToAccountNo())) {
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, command.getFromAccountNo());
        }
    }

    /**
     * 保存转账订单
     */
    private Pair<TradeTransferOrder, TradeTransferOrder> saveTradeTransferOrder(TradeTransferCommand command) {
        Merchant merchantBaseDTO = new Merchant();
        merchantBaseDTO.setMerchantId(command.getFromMerchantId());

        TradeTransferAttributeDTO resultDTO = new TradeTransferAttributeDTO();
        resultDTO.setApplyOperator(command.getApplyOperator());

        String tradeNo = orderNoManager.getTradeNo("", TradeTypeEnum.TRANSFER, merchantBaseDTO.getMerchantId());

        TradeTransferOrder transferFromOrder = new TradeTransferOrder();
        transferFromOrder.setTradeNo(tradeNo);
        transferFromOrder.setPurpose(command.getPurpose());
        transferFromOrder.setDirection(-1);
        transferFromOrder.setMerchantId(command.getFromMerchantId());
        transferFromOrder.setMerchantName(command.getFromMerchantName());
        transferFromOrder.setAccountNo(command.getFromAccountNo());

        // 金额 商户手续费 商户分润 到账金额 通道成本 平台利润
        transferFromOrder.setCurrency(command.getCurrency());
        transferFromOrder.setAmount(command.getAmount());
        transferFromOrder.setMerchantFee(BigDecimal.ZERO);
        transferFromOrder.setMerchantProfit(BigDecimal.ZERO);
        transferFromOrder.setAccountAmount(BigDecimal.ZERO);
        transferFromOrder.setChannelCost(BigDecimal.ZERO);
        transferFromOrder.setPlatformProfit(BigDecimal.ZERO);

        transferFromOrder.setTradeStatus(TradeStatusEnum.TRADE_INIT.getCode());
//        transferFromOrder.setTradeTime(LocalDateTime.now());
        transferFromOrder.setSettleStatus(SettleStatusEnum.SETTLE_TODO.getCode());
        transferFromOrder.setIp("0.0.0.0");
        transferFromOrder.setVersion(TradeConstant.INIT_VERSION);
        transferFromOrder.setCreateTime(LocalDateTime.now());
        transferFromOrder.setAttribute(JSONUtil.toJsonStr(resultDTO));

        TradeTransferOrder transferToOrder = new TradeTransferOrder();
        transferToOrder.setTradeNo(tradeNo);
        transferToOrder.setPurpose(command.getPurpose());
        transferToOrder.setDirection(1);
        transferToOrder.setMerchantId(command.getFromMerchantId());
        transferToOrder.setMerchantName(command.getFromMerchantName());
        transferToOrder.setAccountNo(command.getFromAccountNo());

        // 金额 商户手续费 商户分润 到账金额 通道成本 平台利润
        transferToOrder.setCurrency(command.getCurrency());
        transferToOrder.setAmount(command.getAmount());
        transferToOrder.setMerchantFee(BigDecimal.ZERO);
        transferToOrder.setMerchantProfit(BigDecimal.ZERO);
        transferToOrder.setAccountAmount(BigDecimal.ZERO);
        transferToOrder.setChannelCost(BigDecimal.ZERO);
        transferToOrder.setPlatformProfit(BigDecimal.ZERO);

        transferToOrder.setTradeStatus(TradeStatusEnum.TRADE_INIT.getCode());
        transferToOrder.setTradeTime(System.currentTimeMillis());
        transferToOrder.setSettleStatus(SettleStatusEnum.SETTLE_TODO.getCode());
        transferToOrder.setIp("0.0.0.0");
        transferToOrder.setVersion(TradeConstant.INIT_VERSION);
        transferToOrder.setCreateTime(LocalDateTime.now());
        transferToOrder.setAttribute(JSONUtil.toJsonStr(resultDTO));

        tradeTransferOrderRepository.saveBatch(Arrays.asList(transferFromOrder, transferToOrder));
        return Pair.of(transferFromOrder, transferToOrder);
    }

    /**
     * 同商户之间转账
     */
    private boolean transferWithSameMerchant(Pair<TradeTransferOrder, TradeTransferOrder> transferOrderPair) {
        // 同步 冻结资金
        TradeTransferOrder transferFromOrder = transferOrderPair.getLeft();
        SettleAccountUpdateFrozenCmd frozenCmd = new SettleAccountUpdateFrozenCmd();
        BeanUtils.copyProperties(transferFromOrder, frozenCmd);
        settleAccountCmdService.handlerAccountFrozen(frozenCmd);

        // 处理转账资金操作
        transferSettle(transferOrderPair);

        // 更新数据库 --> 2笔
        UpdateWrapper<TradeTransferOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .eq(TradeTransferOrder::getTradeNo, transferFromOrder.getTradeNo());
        return tradeTransferOrderRepository.update(updateWrapper);
    }


    /**
     * 转账审核
     */
    private void transferReview() {

    }


    /**
     * 发送消息给管理台去审核
     */
    private void sendMsgToReview(TradeTransferOrder order, TradeTransferCommand command) {

    }


    /**
     * 处理转帐审核结果
     */
    private boolean handlerTransferReview(TradeTransferReviewCommand command) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradeTransferOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeTransferOrder::getTradeNo, tradeNo);
        List<TradeTransferOrder> transferOrderList = tradeTransferOrderRepository.list(orderQuery);
        Assert.notEmpty(transferOrderList, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo));

        TradeTransferOrder transferFromOrder = transferOrderList.stream().filter(e -> e.getDirection().equals(-1)).findAny().orElse(null);
        TradeTransferOrder transferToOrder = transferOrderList.stream().filter(e -> e.getDirection().equals(1)).findAny().orElse(null);

        // 判断是否在审核中状态
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(transferFromOrder.getTradeStatus());
        if (!TradeStatusEnum.TRADE_REVIEW.equals(tradeStatusEnum)) {
            log.error("handlerTransferReview orderStatus not in trade review. tradeNo={}", tradeNo);
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo);
        }

        // 判断是否在审核中状态
        tradeStatusEnum = TradeStatusEnum.codeToEnum(transferToOrder.getTradeStatus());
        if (!TradeStatusEnum.TRADE_REVIEW.equals(tradeStatusEnum)) {
            log.error("handlerTransferReview orderStatus not in trade review. tradeNo={}", tradeNo);
            throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo);
        }

        // 先解析TradeResult 构建审核结果
        TradeResultDTO tradeResultDTO =new TradeResultDTO();
        ReviewResultDTO reviewResultDTO = new ReviewResultDTO();
        reviewResultDTO.setReviewStatus(command.isReviewStatus());
        reviewResultDTO.setReviewTime(System.currentTimeMillis());
        reviewResultDTO.setReviewMsg(command.getReviewMsg());
        tradeResultDTO.setReviewResult(reviewResultDTO);

        // 审核驳回
        if (!command.isReviewStatus()) {

            // 解冻资金 ***
            SettleAccountUpdateUnFrozenCmd unFrozenCmd = new SettleAccountUpdateUnFrozenCmd();
            unFrozenCmd.setTradeNo(transferFromOrder.getTradeNo());
            settleAccountCmdService.handlerAccountUnFrozen(unFrozenCmd);

            // 更新订单状态
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(command.getReviewMsg());
            UpdateWrapper<TradeTransferOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeTransferOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .eq(TradeTransferOrder::getTradeNo, transferFromOrder.getTradeNo());
            return tradeTransferOrderRepository.update(updateWrapper);
        }

        // 审核通过 处理转账资金操作

        transferSettle(Pair.of(transferFromOrder, transferToOrder));

        // 审核通过 处理转账订单
        tradeResultDTO.setSuccess(true);
        UpdateWrapper<TradeTransferOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(TradeTransferOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .eq(TradeTransferOrder::getTradeNo, transferFromOrder.getTradeNo());
        return tradeTransferOrderRepository.update(updateWrapper);
    }


    /**
     * 审核成功，提交清结算操作
     */
    private void transferSettle(Pair<TradeTransferOrder, TradeTransferOrder> transferOrderPair) {
        TradeTransferOrder transferFromOrder = transferOrderPair.getLeft();
        TradeTransferOrder transferToOrder = transferOrderPair.getRight();

        SettleAccountUpdateTransferCommand transferCommand = new SettleAccountUpdateTransferCommand();
        transferCommand.setTradeNo(transferFromOrder.getTradeNo());
        transferCommand.setCurrency(transferFromOrder.getCurrency());
        transferCommand.setAmount(transferFromOrder.getAmount());
        transferCommand.setMerchantFee(BigDecimal.ZERO);
        transferCommand.setMerchantProfit(BigDecimal.ZERO);
        transferCommand.setAccountAmount(BigDecimal.ZERO);
        transferCommand.setChannelCost(BigDecimal.ZERO);
        transferCommand.setPlatformProfit(BigDecimal.ZERO);

        transferCommand.setFromMerchantId(transferFromOrder.getMerchantId());
        transferCommand.setFromMerchantName(transferFromOrder.getMerchantName());
        transferCommand.setFromAccountNo(transferFromOrder.getAccountNo());

        transferCommand.setToMerchantId(transferToOrder.getMerchantId());
        transferCommand.setToMerchantName(transferToOrder.getMerchantName());
        transferCommand.setToAccountNo(transferToOrder.getAccountNo());


        settleAccountCmdService.handlerAccountTransfer(transferCommand);
    }

}
