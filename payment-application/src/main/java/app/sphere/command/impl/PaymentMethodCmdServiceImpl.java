package app.sphere.command.impl;

import app.sphere.command.PaymentMethodCmdService;
import app.sphere.command.cmd.PaymentMethodStatusCommand;
import app.sphere.command.cmd.PaymentMethodUpdateCommand;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.PaymentChannelMethodRepository;
import domain.sphere.repository.PaymentMethodRepository;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
import infrastructure.sphere.db.entity.PaymentMethod;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.util.Objects;

@Slf4j
@Service
public class PaymentMethodCmdServiceImpl implements PaymentMethodCmdService {

    @Resource
    PaymentMethodRepository paymentMethodRepository;
    @Resource
    PaymentChannelMethodRepository paymentChannelMethodRepository;

    @Override
    public boolean updatePaymentMethod(PaymentMethodUpdateCommand command) {
        log.info("updatePaymentMethod command={}", JSONUtil.toJsonStr(command));

        PaymentMethod method = paymentMethodRepository.getById(command.getId());
        Assert.notNull(method, () -> new PaymentException(ExceptionCode.PAYMENT_CONFIG_NOT_FOUNT));

        UpdateWrapper<PaymentMethod> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(StringUtils.isNotBlank(command.getPaymentIcon()), PaymentMethod::getPaymentIcon, command.getPaymentIcon())
                .set(Objects.nonNull(command.getPaymentDirection()), PaymentMethod::getPaymentDirection, command.getPaymentDirection())
                .set(Objects.nonNull(command.getPaymentType()), PaymentMethod::getPaymentType, command.getPaymentType())
                .eq(PaymentMethod::getId, method.getId());
        return paymentMethodRepository.update(updateWrapper);
    }

    @Override
    public boolean openOrClosePaymentMethod(PaymentMethodStatusCommand command) {
        log.info("openOrClosePaymentMethod command={}", JSONUtil.toJsonStr(command));

        PaymentMethod method = paymentMethodRepository.getById(command.getId());
        Assert.notNull(method, () -> new PaymentException("payment method not exist"));

        if (!command.getStatus()) {
            UpdateWrapper<PaymentChannelMethod> channelMethodUpdate = new UpdateWrapper<>();
            channelMethodUpdate.lambda().set(PaymentChannelMethod::isStatus, command.getStatus())
                    .eq(PaymentChannelMethod::getPaymentMethod, method.getPaymentMethod());
            paymentChannelMethodRepository.update(channelMethodUpdate);
        }

        UpdateWrapper<PaymentMethod> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(PaymentMethod::isStatus, command.getStatus())
                .eq(PaymentMethod::getId, method.getId());
        return paymentMethodRepository.update(updateWrapper);
    }

}
