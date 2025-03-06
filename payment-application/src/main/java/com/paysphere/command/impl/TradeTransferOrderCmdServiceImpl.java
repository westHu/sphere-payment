package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.TradeTransferOrderCmdService;
import com.paysphere.command.cmd.TradeTransferCommand;
import com.paysphere.command.cmd.TradeTransferReviewCommand;
import com.paysphere.command.dto.TradeTransferAttributeDTO;
import com.paysphere.command.dto.trade.result.ReviewResultDTO;
import com.paysphere.command.dto.trade.result.TradeResultDTO;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.TradeTransferOrder;
import com.paysphere.enums.AreaEnum;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.enums.TradeTransferDirectionEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.manager.OrderNoManager;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.mq.dto.examine.TradeTransferExamineMqMessageDTO;
import com.paysphere.mq.dto.settle.TransferMqMessageDTO;
import com.paysphere.mq.dto.settle.UnfrozenMessageDTO;
import com.paysphere.repository.TradeTransferOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;


@Slf4j
@Service
public class TradeTransferOrderCmdServiceImpl  implements TradeTransferOrderCmdService {

    @Resource
    TradeTransferOrderService tradeTransferOrderService;
    @Resource
    RocketMqProducer rocketMqProducer;
    @Resource
    RedisService redisService;
    @Resource
    OrderNoManager orderNoManager;



    @Override
    public boolean executeTransfer(TradeTransferCommand command) {
        log.info("executeTransfer command={}", JSONUtil.toJsonStr(command));
        return redisService.lock(TradeConstant.LOCK_PREFIX_TRANSFER + command.getTransferOutAccountNo(),
                () -> doTransfer(command));
    }


    @Override
    public void executeTransferReview(TradeTransferReviewCommand command) {
        redisService.lock(TradeConstant.LOCK_PREFIX_TRANSFER_REVIEW + command.getTradeNo(),
                () -> handlerTransferReview(command));
    }

    // ------------------------------------------------------------------------------------------------------

    /**
     * 转账操作
     */
    private boolean doTransfer(TradeTransferCommand command) {
        // 校验参数
        verifyAccountNoNotEqual(command);

        // 保存订单，转账有转出传入，保存[2]笔订单
        TradeTransferOrder transferOutOrder = saveTradeTransferOrder(command);
        String tradeNo = transferOutOrder.getTradeNo();

        String transferOutMerchantId = command.getTransferOutMerchantId();
        String transferToMerchantId = command.getTransferToMerchantId();

        // 如果是同一个商户内部转账, 无需审核
        if (transferOutMerchantId.equals(transferToMerchantId)) {
            log.info("doTransfer same merchant transfer. merchantId={}", transferOutMerchantId);
            return transferWithSameMerchant(transferOutOrder);
        }

        log.info("doTransfer diff merchant transfer. out={}. to={}", transferOutMerchantId, transferToMerchantId);
        // 同步 冻结资金
        transferFrozenAmount(transferOutOrder);

        // 发起审核, Mq给管理台, 状态更为审核中
        transferReview(command, transferOutOrder);

        // 更新交易订单：交易状态-审核中
        UpdateWrapper<TradeTransferOrder> transferOrderUpdate = new UpdateWrapper<>();
        transferOrderUpdate.lambda()
                .set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_REVIEW.getCode())
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradeTransferOrder::getTradeNo, tradeNo);
        return tradeTransferOrderService.update(transferOrderUpdate);
    }

    /**
     * 相关校验
     * 1, 不能自己转自己
     */
    private void verifyAccountNoNotEqual(TradeTransferCommand command) {
        if (command.getTransferOutAccountNo().equals(command.getTransferToAccountNo())) {
            throw new PaymentException(ExceptionCode.TRANSFER_WITH_SAME_ACCOUNT, command.getTransferOutAccountNo());
        }
    }

    /**
     * 保存转账订单
     */
    private TradeTransferOrder saveTradeTransferOrder(TradeTransferCommand command) {
        Merchant merchantBaseDTO = new Merchant();
        merchantBaseDTO.setMerchantId(command.getTransferOutMerchantId());
        merchantBaseDTO.setArea(AreaEnum.INDONESIA.getCode());

        TradeTransferAttributeDTO resultDTO = new TradeTransferAttributeDTO();
        resultDTO.setApplyOperator(command.getApplyOperator());

        String businessNo = orderNoManager.getBusinessNo();
        String tradeNo = orderNoManager.getTradeNo(null, TradeTypeEnum.TRANSFER, merchantBaseDTO.getMerchantId());

        TradeTransferOrder transferOutOrder = new TradeTransferOrder();
        transferOutOrder.setBusinessNo(businessNo);
        transferOutOrder.setTradeNo(tradeNo);
        transferOutOrder.setPurpose(command.getPurpose());
        transferOutOrder.setDirection(1);
        transferOutOrder.setMerchantId(command.getTransferOutMerchantId());
        transferOutOrder.setMerchantName(command.getTransferOutMerchantName());
        transferOutOrder.setAccountNo(command.getTransferOutAccountNo());

        // 金额 商户手续费 商户分润 到账金额 通道成本 平台利润
        transferOutOrder.setCurrency(command.getCurrency());
        transferOutOrder.setAmount(command.getAmount());
        transferOutOrder.setMerchantFee(BigDecimal.ZERO);
        transferOutOrder.setMerchantProfit(BigDecimal.ZERO);
        transferOutOrder.setAccountAmount(BigDecimal.ZERO);
        transferOutOrder.setChannelCost(BigDecimal.ZERO);
        transferOutOrder.setPlatformProfit(BigDecimal.ZERO);

        transferOutOrder.setTradeStatus(TradeStatusEnum.TRADE_INIT.getCode());
//        transferOutOrder.setTradeTime(LocalDateTime.now());
        transferOutOrder.setSettleStatus(SettleStatusEnum.SETTLE_TODO.getCode());
        transferOutOrder.setIp("0.0.0.0");
        transferOutOrder.setVersion(TradeConstant.INIT_VERSION);
        transferOutOrder.setArea(AreaEnum.INDONESIA.getCode());
        transferOutOrder.setCreateTime(LocalDateTime.now());
        transferOutOrder.setAttribute(JSONUtil.toJsonStr(resultDTO));

        TradeTransferOrder transferToOrder = new TradeTransferOrder();
        transferToOrder.setBusinessNo(businessNo);
        transferToOrder.setTradeNo(tradeNo);
        transferToOrder.setPurpose(command.getPurpose());
        transferToOrder.setDirection(1);
        transferToOrder.setMerchantId(command.getTransferOutMerchantId());
        transferToOrder.setMerchantName(command.getTransferOutMerchantName());
        transferToOrder.setAccountNo(command.getTransferOutAccountNo());

        // 金额 商户手续费 商户分润 到账金额 通道成本 平台利润
        transferToOrder.setCurrency(command.getCurrency());
        transferToOrder.setAmount(command.getAmount());
        transferToOrder.setMerchantFee(BigDecimal.ZERO);
        transferToOrder.setMerchantProfit(BigDecimal.ZERO);
        transferToOrder.setAccountAmount(BigDecimal.ZERO);
        transferToOrder.setChannelCost(BigDecimal.ZERO);
        transferToOrder.setPlatformProfit(BigDecimal.ZERO);

        transferToOrder.setTradeStatus(TradeStatusEnum.TRADE_INIT.getCode());
//        transferToOrder.setTradeTime(LocalDateTime.now());
        transferToOrder.setSettleStatus(SettleStatusEnum.SETTLE_TODO.getCode());
        transferToOrder.setIp("0.0.0.0");
        transferToOrder.setVersion(TradeConstant.INIT_VERSION);
        transferToOrder.setArea(AreaEnum.INDONESIA.getCode());
        transferToOrder.setCreateTime(LocalDateTime.now());
        transferToOrder.setAttribute(JSONUtil.toJsonStr(resultDTO));

        tradeTransferOrderService.saveBatch(Arrays.asList(transferOutOrder, transferToOrder));
        return transferOutOrder;
    }

    /**
     * 同商户之间转账
     */
    private boolean transferWithSameMerchant(TradeTransferOrder transferOutOrder) {
        // 同步 冻结资金
        transferFrozenAmount(transferOutOrder);

        // 处理转账资金操作
        sendMsgToTransfer(transferOutOrder);

        // 更新数据库 --> 2笔
        UpdateWrapper<TradeTransferOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .eq(TradeTransferOrder::getTradeNo, transferOutOrder.getTradeNo());
        return tradeTransferOrderService.update(updateWrapper);
    }


    /**
     * 转账审核
     */
    private void transferReview(TradeTransferCommand command, TradeTransferOrder transferOutOrder) {
        String tradeNo = transferOutOrder.getTradeNo();

        try {
            sendMsgToReview(transferOutOrder, command);
        } catch (Exception e) {
            log.error("transferReview tradeNo:{}. failed mq message exception", tradeNo, e);
            // 更新订单状态
            String error = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "Failed to mq message";

            // 解冻资金, 如果此处异常, 放弃吧
            transferUnfrozenAmount(transferOutOrder);

            // 更新交易订单：交易状态
            TradeResultDTO tradeResultDTO = new TradeResultDTO();
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(error);

            UpdateWrapper<TradeTransferOrder> transferOrderUpdate = new UpdateWrapper<>();
            transferOrderUpdate.lambda()
                    .set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeTransferOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .setSql(TradeConstant.VERSION_SQL)
                    .eq(TradeTransferOrder::getId, transferOutOrder.getId());
            tradeTransferOrderService.update(transferOrderUpdate);
            throw new PaymentException(ExceptionCode.TRANSFER_CHANNEL_ERROR, tradeNo, error);
        }
    }

    /**
     * 冻结资金
     */
    private void transferFrozenAmount(TradeTransferOrder transferOutOrder) {
        /*String tradeNo = transferOutOrder.getTradeNo();

        AccountAmountFrozenParam frozenParam = new AccountAmountFrozenParam();
        frozenParam.setBusinessNo(transferOutOrder.getBusinessNo());
        frozenParam.setTradeNo(tradeNo);
        frozenParam.setCurrency(transferOutOrder.getCurrency());
        frozenParam.setAmount(transferOutOrder.getAmount()); // 冻结金额即转账金额
        frozenParam.setMerchantId(transferOutOrder.getMerchantId()); // 转出账户
        frozenParam.setMerchantName(transferOutOrder.getMerchantName());
        frozenParam.setAccountNo(transferOutOrder.getAccountNo());

        try {
            log.info("transferFrozenAmount tradeNo={} frozenParam={}", tradeNo, JSONUtil.toJsonStr(frozenParam));
            Boolean transferFrozen = BaseResult.parse(settleApiService.frozenAmount(frozenParam).toFuture().join());
            log.info("transferFrozenAmount tradeNo={} result={}", tradeNo, transferFrozen);
        } catch (Exception e) {
            log.error("transferFrozenAmount tradeNo={} exception", tradeNo, e);

            // 冻结金额失败 更新交易订单：交易状态
            TradeResultDTO tradeResultDTO = new TradeResultDTO();
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(e.getMessage());
            UpdateWrapper<TradeTransferOrder> transferOrderUpdate = new UpdateWrapper<>();
            transferOrderUpdate.lambda()
                    .set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeTransferOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .eq(TradeTransferOrder::getTradeNo, tradeNo);
            tradeTransferOrderService.update(transferOrderUpdate);
            throw new PaymentException(ExceptionCode.TRANSFER_CHANNEL_ERROR, transferOutOrder.getTradeNo(), e.getMessage());
        }*/

    }


    /**
     * 解冻商户资金账户
     */
    private void transferUnfrozenAmount(TradeTransferOrder order) {
        String tradeNo = order.getTradeNo();

        UnfrozenMessageDTO unfrozenMessageDTO = new UnfrozenMessageDTO();
        unfrozenMessageDTO.setTradeNo(tradeNo);
        unfrozenMessageDTO.setOuterNo(tradeNo);

        log.info("transferUnfrozenAmount unfrozenMessageDTO={}", JSONUtil.toJsonStr(unfrozenMessageDTO));
        SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.UNFROZEN_TOPIC, JSONUtil.toJsonStr(unfrozenMessageDTO));
        log.info("transferUnfrozenAmount tradeNo={}, result={}", tradeNo, sendResult);
    }


    /**
     * 发送消息给管理台去审核
     */
    private void sendMsgToReview(TradeTransferOrder order, TradeTransferCommand command) {
        TradeTransferExamineMqMessageDTO messageDTO = new TradeTransferExamineMqMessageDTO();
        messageDTO.setBusinessNo(order.getBusinessNo());
        messageDTO.setTradeNo(order.getTradeNo());
        messageDTO.setPurpose(order.getPurpose());
        messageDTO.setTransferOutMerchantId(command.getTransferOutMerchantId());
        messageDTO.setTransferOutMerchantName(command.getTransferOutMerchantName());
        messageDTO.setTransferOutAccount(command.getTransferOutAccountNo());
        messageDTO.setTransferToMerchantId(command.getTransferToMerchantId());
        messageDTO.setTransferToMerchantName(command.getTransferToMerchantName());
        messageDTO.setTransferToAccount(command.getTransferToAccountNo());
        messageDTO.setCurrency(order.getCurrency());
        messageDTO.setAmount(order.getAmount());
        messageDTO.setApplyOperator(command.getApplyOperator());

        SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.TRADE_EXAMINE_TOPIC, JSONUtil.toJsonStr(messageDTO));
        log.info("transfer sendMsgToReview sendResult={}", sendResult);
    }


    /**
     * 处理转帐审核结果
     */
    private boolean handlerTransferReview(TradeTransferReviewCommand command) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradeTransferOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeTransferOrder::getTradeNo, tradeNo)
                .eq(TradeTransferOrder::getDirection, TradeTransferDirectionEnum.TRANSFER_OUT.getCode())
                .last(TradeConstant.LIMIT_1);
        TradeTransferOrder transferOutOrder = tradeTransferOrderService.getOne(orderQuery);
        Assert.notNull(transferOutOrder, () -> new PaymentException(ExceptionCode.TRANSFER_ORDER_NOT_EXIST, tradeNo));

        // 判断是否在审核中状态
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(transferOutOrder.getTradeStatus());
        if (!TradeStatusEnum.TRADE_REVIEW.equals(tradeStatusEnum)) {
            log.error("handlerTransferReview orderStatus not in trade review. tradeNo={}", tradeNo);
            throw new PaymentException(ExceptionCode.TRANSFER_ORDER_NOT_IN_REVIEW, tradeNo);
        }

        // 先解析TradeResult 构建审核结果
        TradeResultDTO tradeResultDTO = Optional.of(transferOutOrder)
                .map(TradeTransferOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());
        ReviewResultDTO reviewResultDTO = new ReviewResultDTO();
        reviewResultDTO.setReviewStatus(command.isReviewStatus());
        reviewResultDTO.setReviewTime(LocalDateTime.now());
        reviewResultDTO.setReviewMsg(command.getReviewMsg());
        tradeResultDTO.setReviewResult(reviewResultDTO);

        // 审核驳回
        if (!command.isReviewStatus()) {
            // 解冻资金 ***
            transferUnfrozenAmount(transferOutOrder);

            // 更新订单状态
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(command.getReviewMsg());
            UpdateWrapper<TradeTransferOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeTransferOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .eq(TradeTransferOrder::getId, transferOutOrder.getId());
            return tradeTransferOrderService.update(updateWrapper);
        }

        // 审核通过 处理转账资金操作
        sendMsgToTransfer(transferOutOrder);

        // 审核通过 处理转账订单
        tradeResultDTO.setSuccess(true);
        UpdateWrapper<TradeTransferOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode())
                .set(TradeTransferOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                .eq(TradeTransferOrder::getId, transferOutOrder.getId());
        return tradeTransferOrderService.update(updateWrapper);
    }


    /**
     * 审核成功，提交清结算操作
     */
    private void sendMsgToTransfer(TradeTransferOrder transferOutOrder) {
        String tradeNo = transferOutOrder.getTradeNo();

        TransferMqMessageDTO messageDTO = new TransferMqMessageDTO();
        messageDTO.setBusinessNo(transferOutOrder.getBusinessNo());
        messageDTO.setTradeNo(tradeNo);
        messageDTO.setTransferToMerchantId(transferOutOrder.getMerchantId());
        messageDTO.setTransferToMerchantName(transferOutOrder.getMerchantName());
        messageDTO.setTransferToAccount(transferOutOrder.getAccountNo());

        try {
            log.info("sendMsgToTransfer tradeNo={} messageDTO={}", tradeNo, JSONUtil.toJsonStr(messageDTO));
            SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.TRANSFER_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("sendMsgToTransfer tradeNo={} sendResult={}", tradeNo, sendResult);
        } catch (Exception e) {
            log.error("sendMsgToTransfer tradeNo={} exception", tradeNo, e);

            // 解冻资金
            transferFrozenAmount(transferOutOrder);

            // 冻结金额失败 更新交易订单：交易状态
            TradeResultDTO tradeResultDTO = new TradeResultDTO();
            tradeResultDTO.setSuccess(false);
            tradeResultDTO.setError(e.getMessage());
            UpdateWrapper<TradeTransferOrder> transferOrderUpdate = new UpdateWrapper<>();
            transferOrderUpdate.lambda()
                    .set(TradeTransferOrder::getTradeStatus, TradeStatusEnum.TRADE_FAILED.getCode())
                    .set(TradeTransferOrder::getTradeResult, JSONUtil.toJsonStr(tradeResultDTO))
                    .eq(TradeTransferOrder::getTradeNo, tradeNo);
            tradeTransferOrderService.update(transferOrderUpdate);
            throw new PaymentException(ExceptionCode.TRANSFER_CHANNEL_ERROR, transferOutOrder.getTradeNo(), e.getMessage());
        }
    }

}
