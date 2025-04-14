package app.sphere.command.impl;

import app.sphere.command.PaymentChannelMethodCmdService;
import app.sphere.command.cmd.*;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.*;
import infrastructure.sphere.db.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.sphere.enums.*;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.util.*;

import static share.sphere.TradeConstant.LIMIT_1;


@Slf4j
@Service
public class PaymentChannelMethodCmdServiceImpl implements PaymentChannelMethodCmdService {

    @Resource
    PaymentChannelRepository paymentChannelRepository;
    @Resource
    PaymentChannelMethodRepository paymentChannelMethodRepository;
    @Resource
    PaymentMethodRepository paymentMethodRepository;
    @Resource
    MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository;
    @Resource
    MerchantPayoutChannelConfigRepository merchantPayoutChannelConfigRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean openOrClosePaymentChannelMethod(PaymentChannelMethodStatusCommand command) {
        log.info("openOrClosePaymentChannelMethod command={}", JSONUtil.toJsonStr(command));

        QueryWrapper<PaymentChannelMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(PaymentChannelMethod::getDescription, command.getPaymentDirection())
                .eq(PaymentChannelMethod::getChannelCode, command.getChannelCode())
                .eq(PaymentChannelMethod::getPaymentMethod, command.getPaymentMethod());
        List<PaymentChannelMethod> channelMethodList = paymentChannelMethodRepository.list(queryWrapper);
        Assert.notEmpty(channelMethodList, () -> new PaymentException(ExceptionCode.PAYMENT_CONFIG_NOT_FOUNT));

        //开/关闭商户侧的配置, 执行关闭
        if (!command.getStatus()) {
            PaymentDirectionEnum paymentDirectionEnum = PaymentDirectionEnum.codeToEnum(command.getPaymentDirection());
            TradeTypeEnum tradeTypeEnum = paymentDirectionEnum.getTradeTypeEnum();
            if (TradeTypeEnum.PAYMENT.equals(tradeTypeEnum)) {
                UpdateWrapper<MerchantPaymentChannelConfig> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().set(MerchantPaymentChannelConfig::isStatus, false)
                        .eq(MerchantPaymentChannelConfig::getChannelCode, command.getChannelCode())
                        .eq(MerchantPaymentChannelConfig::getPaymentMethod, command.getPaymentMethod());
                merchantPaymentChannelConfigRepository.update(updateWrapper);
            }

            if (TradeTypeEnum.PAYOUT.equals(tradeTypeEnum)) {
                UpdateWrapper<MerchantPayoutChannelConfig> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().set(MerchantPayoutChannelConfig::isStatus, false)
                        .eq(MerchantPayoutChannelConfig::getChannelCode, command.getChannelCode())
                        .eq(MerchantPayoutChannelConfig::getPaymentMethod, command.getPaymentMethod());
                merchantPayoutChannelConfigRepository.update(updateWrapper);
            }
        }

        //开启关闭渠道侧的配置
        List<Long> collect = channelMethodList.stream().map(BaseEntity::getId).toList();
        UpdateWrapper<PaymentChannelMethod> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(PaymentChannelMethod::isStatus, command.getStatus())
                .in(PaymentChannelMethod::getId, collect);
        return paymentChannelMethodRepository.update(updateWrapper);
    }

    @Override
    public boolean addPaymentChannelMethod(PaymentChannelMethodAddCommand command) {
        log.info("addPaymentChannelMethod command={}", JSONUtil.toJsonStr(command));

        QueryWrapper<PaymentChannel> channelQuery = new QueryWrapper<>();
        channelQuery.lambda().eq(PaymentChannel::getChannelCode, command.getChannelCode()).last(LIMIT_1);
        PaymentChannel channel = paymentChannelRepository.getOne(channelQuery);
        Assert.notNull(channel, () -> new PaymentException(ExceptionCode.PAYMENT_CONFIG_NOT_FOUNT));
        Assert.isTrue(channel.isStatus(), () -> new PaymentException(ExceptionCode.PAYMENT_CONFIG_DISABLED));

        QueryWrapper<PaymentMethod> methodQueryWrapper = new QueryWrapper<>();
        methodQueryWrapper.lambda().eq(PaymentMethod::getPaymentMethod, command.getPaymentMethod()).last(LIMIT_1);
        PaymentMethod paymentMethod = paymentMethodRepository.getOne(methodQueryWrapper);
        Assert.notNull(paymentMethod, () -> new PaymentException(ExceptionCode.PAYMENT_CONFIG_NOT_FOUNT));
        Assert.isTrue(paymentMethod.isStatus(), () -> new PaymentException(ExceptionCode.PAYMENT_CONFIG_DISABLED));

        QueryWrapper<PaymentChannelMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(PaymentChannelMethod::getChannelCode, command.getChannelCode())
                .eq(PaymentChannelMethod::getPaymentMethod, command.getPaymentMethod())
                .eq(PaymentChannelMethod::getPaymentDirection, command.getPaymentDirection()).last(LIMIT_1);
        PaymentChannelMethod channelMethod = paymentChannelMethodRepository.getOne(queryWrapper);
        Assert.isNull(channelMethod, () -> new PaymentException(ExceptionCode.PAYMENT_CONFIG_HAS_EXIST));

        String settleTime = Optional.of(command).map(PaymentChannelMethodAddCommand::getSettleTime)
                .map(SettleTimeEnum::getByTime)
                .map(SettleTimeEnum::getTime)
                .orElse(null);
        channelMethod = new PaymentChannelMethod();
        channelMethod.setChannelCode(command.getChannelCode());
        channelMethod.setChannelName(channel.getChannelName());
        channelMethod.setSettleType(command.getSettleType());
        channelMethod.setSettleTime(settleTime);
        channelMethod.setPaymentMethod(command.getPaymentMethod());
        channelMethod.setPaymentDirection(command.getPaymentDirection());
        channelMethod.setPaymentAttribute(command.getPaymentAttribute());
        channelMethod.setDescription(command.getDescription());
        channelMethod.setSingleFee(command.getSingleFee());
        channelMethod.setSingleRate(command.getSingleRate());
        channelMethod.setAmountLimitMin(command.getAmountLimitMin());
        channelMethod.setAmountLimitMax(command.getAmountLimitMax());
        return paymentChannelMethodRepository.save(channelMethod);
    }


    @Override
    public boolean updatePaymentChannelMethod(PaymentChannelMethodUpdateCommand command) {
        PaymentChannelMethod channelMethod = paymentChannelMethodRepository.getById(command.getId());
        Assert.notNull(channelMethod, () -> new PaymentException(ExceptionCode.PAYMENT_CONFIG_NOT_FOUNT));

        UpdateWrapper<PaymentChannelMethod> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(StringUtils.isNotBlank(command.getPaymentAttribute()),
                        PaymentChannelMethod::getPaymentAttribute, command.getPaymentAttribute())
                .set(Objects.nonNull(command.getSingleFee()), PaymentChannelMethod::getSingleFee, command.getSingleFee())
                .set(Objects.nonNull(command.getSingleRate()), PaymentChannelMethod::getSingleRate, command.getSingleRate())
                .set(Objects.nonNull(command.getAmountLimitMin()), PaymentChannelMethod::getAmountLimitMin, command.getAmountLimitMin())
                .set(Objects.nonNull(command.getAmountLimitMax()), PaymentChannelMethod::getAmountLimitMax, command.getAmountLimitMax())
                .eq(PaymentChannelMethod::getId, channelMethod.getId());
        return paymentChannelMethodRepository.update(updateWrapper);
    }

}
