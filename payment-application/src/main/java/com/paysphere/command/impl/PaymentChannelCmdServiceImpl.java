package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.command.PaymentChannelCmdService;
import com.paysphere.command.cmd.PaymentChannelStatusCommand;
import com.paysphere.command.cmd.PaymentChannelUpdateCommand;
import com.paysphere.db.entity.PaymentChannel;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.exception.PaymentException;
import com.paysphere.repository.PaymentChannelMethodService;
import com.paysphere.repository.PaymentChannelService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class PaymentChannelCmdServiceImpl implements PaymentChannelCmdService {

    @Resource
    PaymentChannelService paymentChannelService;
    @Resource
    PaymentChannelMethodService paymentChannelMethodService;

    @Override
    public boolean updatePaymentChannel(PaymentChannelUpdateCommand command) {
        PaymentChannel channel = paymentChannelService.getById(command.getId());
        Assert.notNull(channel, () -> new PaymentException("payment channel not exist"));

        UpdateWrapper<PaymentChannel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(StringUtils.isNotBlank(command.getUrl()), PaymentChannel::getUrl, command.getUrl())
                .set(StringUtils.isNotBlank(command.getLicense()), PaymentChannel::getLicense, command.getLicense())
                .eq(PaymentChannel::getId, channel.getId());
        return paymentChannelService.update(updateWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean openOrClosePaymentChannel(PaymentChannelStatusCommand command) {
        PaymentChannel channel = paymentChannelService.getById(command.getId());
        Assert.notNull(channel, () -> new PaymentException("payment channel not exist"));

        if (command.getRelationMethod()) {
            UpdateWrapper<PaymentChannelMethod> channelMethodUpdate = new UpdateWrapper<>();
            channelMethodUpdate.lambda().set(PaymentChannelMethod::isStatus, command.getStatus())
                    .eq(PaymentChannelMethod::getChannelCode, channel.getChannelCode());
            paymentChannelMethodService.update(channelMethodUpdate);
        }

        UpdateWrapper<PaymentChannel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(PaymentChannel::isStatus, command.getStatus())
                .set(StringUtils.isNotBlank(command.getUrl()), PaymentChannel::getUrl, command.getUrl())
                .set(StringUtils.isNotBlank(command.getLicense()), PaymentChannel::getLicense, command.getLicense())
                .eq(PaymentChannel::getId, channel.getId());
        paymentChannelService.update(updateWrapper);

        return true;
    }

}
