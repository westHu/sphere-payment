package app.sphere.command.impl;

import app.sphere.command.MerchantChannelConfigCmdService;
import app.sphere.command.cmd.MerchantChannelConfigUpdateCmd;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.MerchantPaymentChannelConfigRepository;
import domain.sphere.repository.MerchantPayoutChannelConfigRepository;
import infrastructure.sphere.db.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MerchantChannelConfigCmdServiceImpl implements MerchantChannelConfigCmdService {

    @Resource
    MerchantPaymentChannelConfigRepository MerchantPaymentChannelConfigRepository;
    @Resource
    MerchantPayoutChannelConfigRepository MerchantPayoutChannelConfigRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMerchantChannel(MerchantChannelConfigUpdateCmd cmd) {
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.codeToEnum(cmd.getTradeType());
        log.info("updateMerchantChannel tradeTypeEnum={}", tradeTypeEnum.name());

        String merchantId = cmd.getMerchantId();
        String paymentMethod = cmd.getPaymentMethod();
        String channelCode = cmd.getChannelCode();

        if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
            QueryWrapper<MerchantPaymentChannelConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(MerchantPaymentChannelConfig::getMerchantId, merchantId)
                    .eq(MerchantPaymentChannelConfig::getPaymentMethod, paymentMethod)
                    .eq(StringUtils.isNotBlank(channelCode), MerchantPaymentChannelConfig::getChannelCode, channelCode);
            List<MerchantPaymentChannelConfig> configList = MerchantPaymentChannelConfigRepository.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(configList)) {
                List<Long> collect = configList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                UpdateWrapper<MerchantPaymentChannelConfig> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda()
                        .set(Objects.nonNull(cmd.getSingleRate()), MerchantPaymentChannelConfig::getSingleRate, cmd.getSingleRate())
                        .set(Objects.nonNull(cmd.getSingleFee()), MerchantPaymentChannelConfig::getSingleFee, cmd.getSingleFee())
                        .set(Objects.nonNull(cmd.getSettleType()), MerchantPaymentChannelConfig::getSettleType, cmd.getSettleType())
                        .set(Objects.nonNull(cmd.getSettleTime()), MerchantPaymentChannelConfig::getSettleTime, cmd.getSettleTime())
                        .set(Objects.nonNull(cmd.getStatus()), MerchantPaymentChannelConfig::isStatus, cmd.getStatus())
                        .in(MerchantPaymentChannelConfig::getId, collect);
                return MerchantPaymentChannelConfigRepository.update(updateWrapper);
            }

        } else if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {

            QueryWrapper<MerchantPayoutChannelConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(MerchantPayoutChannelConfig::getMerchantId, merchantId)
                    .eq(MerchantPayoutChannelConfig::getPaymentMethod, paymentMethod)
                    .eq(StringUtils.isNotBlank(channelCode), MerchantPayoutChannelConfig::getChannelCode, channelCode);
            List<MerchantPayoutChannelConfig> configList = MerchantPayoutChannelConfigRepository.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(configList)) {
                List<Long> collect = configList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                UpdateWrapper<MerchantPayoutChannelConfig> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda()
                        .set(Objects.nonNull(cmd.getSingleRate()), MerchantPayoutChannelConfig::getSingleRate, cmd.getSingleRate())
                        .set(Objects.nonNull(cmd.getSingleFee()), MerchantPayoutChannelConfig::getSingleFee, cmd.getSingleFee())
                        .set(Objects.nonNull(cmd.getSettleType()), MerchantPayoutChannelConfig::getSettleType, cmd.getSettleType())
                        .set(Objects.nonNull(cmd.getSettleTime()), MerchantPayoutChannelConfig::getSettleTime, cmd.getSettleTime())
                        .set(Objects.nonNull(cmd.getStatus()), MerchantPayoutChannelConfig::isStatus, cmd.getStatus())
                        .in(MerchantPayoutChannelConfig::getId, collect);
                return MerchantPayoutChannelConfigRepository.update(updateWrapper);
            }
        }

        throw new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeTypeEnum.name());
    }
}
