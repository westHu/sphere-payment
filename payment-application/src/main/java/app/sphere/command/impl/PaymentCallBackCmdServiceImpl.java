package app.sphere.command.impl;

import app.sphere.command.PaymentCallBackCmdService;
import app.sphere.command.cmd.PaymentFinishCommand;
import app.sphere.command.cmd.callback.MockDisbursementCallBackCommand;
import app.sphere.command.cmd.callback.MockTransactionCallBackCommand;
import app.sphere.command.dto.trade.result.PaymentResultDTO;
import app.sphere.command.handler.PaymentFinish4PaymentHandler;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import domain.sphere.repository.PaymentCallBackMessageRepository;
import domain.sphere.repository.TradePaymentOrderRepository;
import infrastructure.sphere.db.entity.PaymentCallBackMessage;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import infrastructure.sphere.remote.channel.BaseCallBackDTO;
import infrastructure.sphere.remote.channel.ChannelEnum;
import infrastructure.sphere.remote.channel.ChannelResult;
import infrastructure.sphere.remote.channel.mock.MockChannelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.exception.PaymentException;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

import static share.sphere.TradeConstant.LIMIT_1;


@Slf4j
@Service
public class PaymentCallBackCmdServiceImpl implements PaymentCallBackCmdService {

    @Resource
    TradePaymentOrderRepository tradePaymentOrderRepository;
    @Resource
    PaymentCallBackMessageRepository paymentCallBackMessageRepository;
    @Resource
    MockChannelService mockChannelService;
    @Resource
    PaymentFinish4PaymentHandler paymentFinish4PaymentHandler;

    @Override
    public String callBackTransaction4Mock(String param) {
        //保存消息
        MockTransactionCallBackCommand command = JSONUtil.toBean(param, MockTransactionCallBackCommand.class);
        String txId = command.getTxId();
        String tradeNo = command.getTradeNo();
        saveCallBackMessage(ChannelEnum.C_MOCK, tradeNo, txId, param);

       //成功，失败原因
        Integer status = command.getStatus();
        String message = command.getMessage();

        //验证签名  - 通过交易单号查询订单
        BaseCallBackDTO<MockTransactionCallBackCommand> callBackDTO = new BaseCallBackDTO<>();
        callBackDTO.setTradeNo(tradeNo);
        callBackDTO.setChannelTime(LocalDateTime.now());
        callBackDTO.setChannelStatus(status);
        callBackDTO.setChannelError(message);
        callBackDTO.setPayload(command);
        ChannelResult<String> channelResult = mockChannelService.transactionCallBack(callBackDTO);

        //验证通过处理业务
        if (channelResult.isSuccess()) {
            transactionCallBack(callBackDTO);
        }
        return channelResult.getPayload();
    }

    @Override
    public String callBackDisbursement4Mock(String param) {
        MockDisbursementCallBackCommand command = JSONUtil.toBean(param, MockDisbursementCallBackCommand.class);
        String txId = command.getTxId();
        String tradeNo = command.getTradeNo();
        saveCallBackMessage(ChannelEnum.C_MOCK, tradeNo, txId, param);

        //成功，失败原因
        Integer status = command.getStatus();
        String message = command.getMessage();

        //验证签名  - 通过交易单号查询订单
        BaseCallBackDTO<MockDisbursementCallBackCommand> callBackDTO = new BaseCallBackDTO<>();
        callBackDTO.setTradeNo(tradeNo);
        callBackDTO.setChannelTime(LocalDateTime.now());
        callBackDTO.setChannelStatus(status);
        callBackDTO.setChannelError(message);
        callBackDTO.setPayload(command);
        ChannelResult<String> channelResult = mockChannelService.transactionCallBack(callBackDTO);

        //验证通过处理业务
        if (channelResult.isSuccess()) {
            transactionCallBack(callBackDTO);
        }
        return channelResult.getPayload();
    }


    /**
     * 保存回调消息
     */
    private void saveCallBackMessage(ChannelEnum channel, String tradeNo, String channelOrderNo, String msg) {
        PaymentCallBackMessage message = new PaymentCallBackMessage();
        message.setChannelCode(channel.getName());
        message.setChannelName(channel.getName());
        message.setTradeNo(tradeNo);
        message.setChannelOrderNo(channelOrderNo);
        message.setMessage(msg);
        paymentCallBackMessageRepository.save(message);
    }

    /**
     * 收款回调-业务处理
     */
    public void transactionCallBack(BaseCallBackDTO<?> dto) {
        String tradeNo = dto.getTradeNo();
        String paymentNo = dto.getPaymentNo();
        String channelOrderNo = dto.getChannelOrderNo();
        log.info("transactionCallBack tradeNo={}, paymentNo={}, channelOrderNo:{}", tradeNo, paymentNo, channelOrderNo);

        //查询收款订单, 必须存在
        QueryWrapper<TradePaymentOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(tradeNo), TradePaymentOrder::getTradeNo, tradeNo)
                .eq(StringUtils.isNotBlank(channelOrderNo), TradePaymentOrder::getChannelOrderNo, channelOrderNo)
                .last(LIMIT_1);
        TradePaymentOrder order = tradePaymentOrderRepository.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException("transactionCallBack order not exist. " + channelOrderNo));

        //如果是终态、成功/失败 则报告并忽略
        if (PaymentStatusEnum.getFinalStatus().contains(order.getPaymentStatus()) && !dto.isIgnoreFinalStatus()) {
            log.error("transactionCallBack warning : status already final status with :{}", order.getTradeNo());
            return;
        }

        //是否需要校验 状态为支付中？ 认为收款没有必要. 成功直接更新为成功并回调交易中心
        //处理数据库
        UpdateWrapper<TradePaymentOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(TradePaymentOrder::getPaymentStatus, dto.getChannelStatus())
                .set(TradePaymentOrder::getPaymentFinishTime, dto.getChannelTime());

        //如果不成功，则需要把失败原因更新到remark
        if (!PaymentStatusEnum.PAYMENT_SUCCESS.getCode().equals(dto.getChannelStatus())) {
            PaymentResultDTO paymentResultDTO = Optional.of(order).map(TradePaymentOrder::getPaymentResult)
                    .map(e -> JSONUtil.toBean(e, PaymentResultDTO.class))
                    .orElse(new PaymentResultDTO());
            paymentResultDTO.setErrorMsg(dto.getChannelError());
            updateWrapper.lambda().set(TradePaymentOrder::getPaymentResult, JSONUtil.toJsonStr(paymentResultDTO));
        }

        updateWrapper.lambda().eq(TradePaymentOrder::getId, order.getId());
        tradePaymentOrderRepository.update(updateWrapper);

        //结算、回调
        paymentFinish4PaymentHandler.handlerPaymentFinish4Payment(order);
    }
}
