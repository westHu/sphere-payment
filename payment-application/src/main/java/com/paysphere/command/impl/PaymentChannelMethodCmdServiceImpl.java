package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.command.PaymentChannelMethodCmdService;
import com.paysphere.command.cmd.PaymentChannelMethodAddCommand;
import com.paysphere.command.cmd.PaymentChannelMethodStatusCommand;
import com.paysphere.command.cmd.PaymentChannelMethodUpdateCommand;
import com.paysphere.db.entity.PaymentChannel;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.db.entity.PaymentMethod;
import com.paysphere.exception.PaymentException;
import com.paysphere.repository.PaymentChannelMethodService;
import com.paysphere.repository.PaymentChannelService;
import com.paysphere.repository.PaymentMethodService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.paysphere.TradeConstant.LIMIT_1;


@Slf4j
@Service
public class PaymentChannelMethodCmdServiceImpl implements PaymentChannelMethodCmdService {

    @Resource
    PaymentChannelService paymentChannelService;
    @Resource
    PaymentChannelMethodService paymentChannelMethodService;
    @Resource
    PaymentMethodService paymentMethodService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean openOrClosePaymentChannelMethod(List<PaymentChannelMethodStatusCommand> commandList) {
        commandList.forEach(command -> {
            PaymentChannelMethod channelMethod = paymentChannelMethodService.getById(command.getId());
            Assert.notNull(channelMethod, () -> new PaymentException("channel method not exist"));

            //如果想打开某个渠道支付方式，然而渠道关闭，则忽略此操作
            String channelCode = channelMethod.getChannelCode();
            String paymentMethod = channelMethod.getPaymentMethod();
            QueryWrapper<PaymentChannel> channelQuery = new QueryWrapper<>();
            channelQuery.lambda().eq(PaymentChannel::getChannelCode, channelCode).last(LIMIT_1);
            PaymentChannel channel = paymentChannelService.getOne(channelQuery);
            if (Objects.isNull(channel)) return;
            if (command.getStatus() && !channel.isStatus()) {
                log.warn("warning. channel status is closed, cannot to open. {} {}", channelCode, paymentMethod);
                return;
            }

            //开启关闭商户侧的配置
//            MerchantChannelConfigUpdateByPaymentParam paymentParam = new MerchantChannelConfigUpdateByPaymentParam();
//            paymentParam.setPaymentDirection(channelMethod.getPaymentDirection());
//            paymentParam.setPaymentMethod(paymentMethod);
//            paymentParam.setChannelCode(channelCode);
//            paymentParam.setStatus(command.getStatus());
//            merchantOpenFeignService.updateMerchantChannelStatusByPayment(paymentParam);

            //开启关闭渠道侧的配置
            UpdateWrapper<PaymentChannelMethod> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(PaymentChannelMethod::isStatus, command.getStatus())
                    .eq(PaymentChannelMethod::getId, command.getId());
            paymentChannelMethodService.update(updateWrapper);
        });
        return true;
    }

    @Override
    public boolean addPaymentChannelMethod(PaymentChannelMethodAddCommand command) {
        QueryWrapper<PaymentChannel> channelQuery = new QueryWrapper<>();
        channelQuery.lambda().eq(PaymentChannel::getChannelCode, command.getChannelCode()).last(LIMIT_1);
        PaymentChannel channel = paymentChannelService.getOne(channelQuery);
        Assert.notNull(channel, () -> new PaymentException("channel not exist"));

        Assert.isTrue(channel.isStatus(), () -> new PaymentException("channel not invalid"));

        QueryWrapper<PaymentChannelMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PaymentChannelMethod::getChannelCode, command.getChannelCode())
                .eq(PaymentChannelMethod::getPaymentMethod, command.getPaymentMethod())
                .eq(PaymentChannelMethod::getPaymentDirection, command.getPaymentDirection()).last(LIMIT_1);
        PaymentChannelMethod channelMethod = paymentChannelMethodService.getOne(queryWrapper);
        Assert.isNull(channelMethod, () -> new PaymentException("channel method has exist"));

        QueryWrapper<PaymentMethod> methodQueryWrapper = new QueryWrapper<>();
        methodQueryWrapper.lambda().eq(PaymentMethod::getPaymentMethod, command.getPaymentMethod()).last(LIMIT_1);
        PaymentMethod paymentMethod = paymentMethodService.getOne(methodQueryWrapper);
        Assert.notNull(paymentMethod, () -> new PaymentException("payment method not exist"));

        channelMethod = new PaymentChannelMethod();
        channelMethod.setChannelCode(command.getChannelCode());
        channelMethod.setChannelName(channel.getChannelName());
        channelMethod.setSettleType(command.getSettleType());
        channelMethod.setSettleTime(StringUtils.equals(command.getSettleType(), "D0") ? command.getSettleTime() : "00:00:00");
        channelMethod.setPaymentMethod(command.getPaymentMethod());
        channelMethod.setPaymentDirection(command.getPaymentDirection());
        channelMethod.setPaymentAttribute(command.getPaymentAttribute());
        channelMethod.setDescription(command.getDescription());
        channelMethod.setSingleFee(command.getSingleFee());
        channelMethod.setSingleRate(command.getSingleRate());
        channelMethod.setAmountLimitMin(command.getAmountLimitMin());
        channelMethod.setAmountLimitMax(command.getAmountLimitMax());
        return paymentChannelMethodService.save(channelMethod);
    }


    @Override
    public boolean updatePaymentChannelMethod(PaymentChannelMethodUpdateCommand command) {
        PaymentChannelMethod channelMethod = paymentChannelMethodService.getById(command.getId());
        Assert.notNull(channelMethod, () -> new PaymentException("channel method not exist"));

        UpdateWrapper<PaymentChannelMethod> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(StringUtils.isNotBlank(command.getPaymentAttribute()),
                        PaymentChannelMethod::getPaymentAttribute, command.getPaymentAttribute())
                .set(Objects.nonNull(command.getSingleFee()), PaymentChannelMethod::getSingleFee,
                        command.getSingleFee())
                .set(Objects.nonNull(command.getSingleRate()), PaymentChannelMethod::getSingleRate,
                        command.getSingleRate())
                .set(Objects.nonNull(command.getAmountLimitMin()), PaymentChannelMethod::getAmountLimitMin,
                        command.getAmountLimitMin())
                .set(Objects.nonNull(command.getAmountLimitMax()), PaymentChannelMethod::getAmountLimitMax,
                        command.getAmountLimitMax())
                .eq(PaymentChannelMethod::getId, channelMethod.getId());
        return paymentChannelMethodService.update(updateWrapper);
    }

    @Override
    public boolean deletePaymentChannelMethod(Long id) {
        return paymentChannelMethodService.removeById(id);
    }

}
