package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.command.PaymentMethodCmdService;
import com.paysphere.command.cmd.PaymentMethodStatusCommand;
import com.paysphere.command.cmd.PaymentMethodUpdateCommand;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.db.entity.PaymentMethod;
import com.paysphere.exception.PaymentException;
import com.paysphere.repository.PaymentChannelMethodService;
import com.paysphere.repository.PaymentMethodService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class PaymentMethodCmdServiceImpl implements PaymentMethodCmdService {

    @Resource
    PaymentMethodService paymentMethodService;
    @Resource
    PaymentChannelMethodService paymentChannelMethodService;

    @Override
    public boolean updatePaymentMethod(PaymentMethodUpdateCommand command) {
        log.info("updatePaymentMethod command={}", JSONUtil.toJsonStr(command));

        PaymentMethod method = paymentMethodService.getById(command.getId());
        Assert.notNull(method, () -> new PaymentException("payment method not exist"));

        UpdateWrapper<PaymentMethod> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(StringUtils.isNotBlank(command.getPaymentName()), PaymentMethod::getPaymentName,
                        command.getPaymentName())
                .set(StringUtils.isNotBlank(command.getPaymentIcon()), PaymentMethod::getPaymentIcon,
                        command.getPaymentIcon())
                .set(Objects.nonNull(command.getPaymentDirection()), PaymentMethod::getPaymentDirection,
                        command.getPaymentDirection())
                .set(Objects.nonNull(command.getPaymentType()), PaymentMethod::getPaymentType, command.getPaymentType())
                .eq(PaymentMethod::getId, method.getId());
        return paymentMethodService.update(updateWrapper);
    }

    @Override
    public boolean openOrClosePaymentMethod(PaymentMethodStatusCommand command) {
        log.info("openOrClosePaymentMethod command={}", JSONUtil.toJsonStr(command));

        PaymentMethod method = paymentMethodService.getById(command.getId());
        Assert.notNull(method, () -> new PaymentException("payment method not exist"));

        if (command.isRelationMethod()) {
            UpdateWrapper<PaymentChannelMethod> channelMethodUpdate = new UpdateWrapper<>();
            channelMethodUpdate.lambda().set(PaymentChannelMethod::isStatus, command.getStatus())
                    .eq(PaymentChannelMethod::getPaymentMethod, method.getPaymentMethod());
            paymentChannelMethodService.update(channelMethodUpdate);
        }

        UpdateWrapper<PaymentMethod> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(PaymentMethod::isStatus, command.getStatus())
                .eq(PaymentMethod::getId, method.getId());
        return paymentMethodService.update(updateWrapper);
    }

}
