package app.sphere.command.impl;

import app.sphere.command.PaymentChannelCmdService;
import app.sphere.command.cmd.PaymentChannelStatusCommand;
import app.sphere.command.cmd.PaymentChannelUpdateCommand;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.PaymentChannelMethodRepository;
import domain.sphere.repository.PaymentChannelRepository;
import infrastructure.sphere.db.entity.PaymentChannel;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

@Service
public class PaymentChannelCmdServiceImpl implements PaymentChannelCmdService {

    @Resource
    PaymentChannelRepository paymentChannelRepository;
    @Resource
    PaymentChannelMethodRepository paymentChannelMethodRepository;

    @Override
    public boolean updatePaymentChannel(PaymentChannelUpdateCommand command) {
        PaymentChannel channel = paymentChannelRepository.getById(command.getId());
        Assert.notNull(channel, () -> new PaymentException(ExceptionCode.PAYMENT_CONFIG_NOT_FOUNT));

        UpdateWrapper<PaymentChannel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(StringUtils.isNotBlank(command.getUrl()), PaymentChannel::getUrl, command.getUrl())
                .set(StringUtils.isNotBlank(command.getLicense()), PaymentChannel::getLicense, command.getLicense())
                .eq(PaymentChannel::getId, channel.getId());
        return paymentChannelRepository.update(updateWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean openOrClosePaymentChannel(PaymentChannelStatusCommand command) {
        PaymentChannel channel = paymentChannelRepository.getById(command.getId());
        Assert.notNull(channel, () -> new PaymentException(ExceptionCode.PAYMENT_CONFIG_NOT_FOUNT));

        if (command.getRelationMethod()) {
            UpdateWrapper<PaymentChannelMethod> channelMethodUpdate = new UpdateWrapper<>();
            channelMethodUpdate.lambda()
                    .set(PaymentChannelMethod::isStatus, command.getStatus())
                    .eq(PaymentChannelMethod::getChannelCode, channel.getChannelCode());
            paymentChannelMethodRepository.update(channelMethodUpdate);
        }

        UpdateWrapper<PaymentChannel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(PaymentChannel::isStatus, command.getStatus())
                .set(StringUtils.isNotBlank(command.getUrl()), PaymentChannel::getUrl, command.getUrl())
                .set(StringUtils.isNotBlank(command.getLicense()), PaymentChannel::getLicense, command.getLicense())
                .eq(PaymentChannel::getId, channel.getId());
        return paymentChannelRepository.update(updateWrapper);
    }

}
