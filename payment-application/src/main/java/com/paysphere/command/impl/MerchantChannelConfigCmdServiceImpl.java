package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.command.MerchantChannelConfigCmdService;
import com.paysphere.command.cmd.MerchantChannelConfigUpdateCmd;
import com.paysphere.command.cmd.MerchantIdCommand;
import com.paysphere.db.entity.BaseEntity;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantPaymentChannelConfig;
import com.paysphere.db.entity.MerchantPayoutChannelConfig;
import com.paysphere.db.entity.MerchantTemplatePaymentChannelConfig;
import com.paysphere.db.entity.MerchantTemplatePayoutChannelConfig;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.repository.MerchantPaymentChannelConfigService;
import com.paysphere.repository.MerchantPayoutChannelConfigService;
import com.paysphere.repository.MerchantService;
import com.paysphere.repository.MerchantTemplatePaymentChannelConfigService;
import com.paysphere.repository.MerchantTemplatePayoutChannelConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantChannelConfigCmdServiceImpl implements MerchantChannelConfigCmdService {

    @Resource
    MerchantPaymentChannelConfigService MerchantPaymentChannelConfigService;
    @Resource
    MerchantPayoutChannelConfigService MerchantPayoutChannelConfigService;
    @Resource
    MerchantTemplatePaymentChannelConfigService merchantTemplatePaymentChannelConfigService;
    @Resource
    MerchantTemplatePayoutChannelConfigService merchantTemplatePayoutChannelConfigService;
    @Resource
    MerchantService merchantService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMerchantChannelStatus(MerchantChannelConfigUpdateCmd cmd) {
        Assert.notNull(cmd.getStatus(), () -> new PaymentException(ExceptionCode.PARAM_IS_REQUIRED, "status"));
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.codeToEnum(cmd.getTradeType());
        log.info("updateMerchantChannelStatus tradeTypeEnum={}", tradeTypeEnum.name());

        String merchantId = cmd.getMerchantId();
        String paymentMethod = cmd.getPaymentMethod();
        String channelCode = cmd.getChannelCode();

        if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
            QueryWrapper<MerchantPaymentChannelConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(MerchantPaymentChannelConfig::getMerchantId, merchantId)
                    .eq(MerchantPaymentChannelConfig::getPaymentMethod, paymentMethod)
                    .eq(StringUtils.isNotBlank(channelCode), MerchantPaymentChannelConfig::getChannelCode, channelCode);
            List<MerchantPaymentChannelConfig> configList = MerchantPaymentChannelConfigService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(configList)) {
                List<Long> collect = configList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                UpdateWrapper<MerchantPaymentChannelConfig> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().set(MerchantPaymentChannelConfig::isStatus, cmd.getStatus())
                        .in(MerchantPaymentChannelConfig::getId, collect);
                return MerchantPaymentChannelConfigService.update(updateWrapper);
            }

        } else if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {

            QueryWrapper<MerchantPayoutChannelConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(MerchantPayoutChannelConfig::getMerchantId, merchantId)
                    .eq(MerchantPayoutChannelConfig::getPaymentMethod, paymentMethod)
                    .eq(StringUtils.isNotBlank(channelCode), MerchantPayoutChannelConfig::getChannelCode, channelCode);
            List<MerchantPayoutChannelConfig> configList = MerchantPayoutChannelConfigService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(configList)) {
                List<Long> collect = configList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                UpdateWrapper<MerchantPayoutChannelConfig> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().set(MerchantPayoutChannelConfig::isStatus, cmd.getStatus())
                        .in(MerchantPayoutChannelConfig::getId, collect);
                return MerchantPayoutChannelConfigService.update(updateWrapper);
            }

        } else {
            throw new PaymentException(ExceptionCode.UNSUPPORTED_TRADE_TYPE, tradeTypeEnum.name());
        }

        return true;
    }

    @Override
    public boolean updateMerchantChannelPriority(MerchantChannelConfigUpdateCmd command) {
        Assert.notNull(command.getPriority(), () -> new PaymentException(ExceptionCode.PARAM_IS_REQUIRED, "priority"));
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.codeToEnum(command.getTradeType());
        log.info("updateMerchantChannelPriority tradeTypeEnum={}", tradeTypeEnum.name());

        String merchantId = command.getMerchantId();
        String paymentMethod = command.getPaymentMethod();
        String channelCode = command.getChannelCode();

        if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
            QueryWrapper<MerchantPaymentChannelConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(MerchantPaymentChannelConfig::getMerchantId, merchantId)
                    .eq(MerchantPaymentChannelConfig::getPaymentMethod, paymentMethod)
                    .eq(StringUtils.isNotBlank(channelCode), MerchantPaymentChannelConfig::getChannelCode, channelCode);
            List<MerchantPaymentChannelConfig> configList = MerchantPaymentChannelConfigService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(configList)) {
                List<Long> collect = configList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                UpdateWrapper<MerchantPaymentChannelConfig> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().set(MerchantPaymentChannelConfig::getPriority, command.getPriority())
                        .in(MerchantPaymentChannelConfig::getId, collect);
                return MerchantPaymentChannelConfigService.update(updateWrapper);
            }

        } else if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {

            QueryWrapper<MerchantPayoutChannelConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(MerchantPayoutChannelConfig::getMerchantId, merchantId)
                    .eq(MerchantPayoutChannelConfig::getPaymentMethod, paymentMethod)
                    .eq(StringUtils.isNotBlank(channelCode), MerchantPayoutChannelConfig::getChannelCode, channelCode);
            List<MerchantPayoutChannelConfig> configList = MerchantPayoutChannelConfigService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(configList)) {
                List<Long> collect = configList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                UpdateWrapper<MerchantPayoutChannelConfig> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().set(MerchantPayoutChannelConfig::getPriority, command.getPriority())
                        .in(MerchantPayoutChannelConfig::getId, collect);
                return MerchantPayoutChannelConfigService.update(updateWrapper);
            }

        } else {
            throw new PaymentException(ExceptionCode.UNSUPPORTED_TRADE_TYPE, tradeTypeEnum.name());
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMerchantChannelFee(MerchantChannelConfigUpdateCmd cmd) {
        log.info("updateMerchantChannelFee cmd={}", JSONUtil.toJsonStr(cmd));
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.codeToEnum(cmd.getTradeType());
        log.info("updateMerchantChannelFee tradeTypeEnum={}", tradeTypeEnum.name());

        String merchantId = cmd.getMerchantId();
        String paymentMethod = cmd.getPaymentMethod();
        String channelCode = cmd.getChannelCode();
        BigDecimal singleFee = cmd.getSingleFee();
        BigDecimal singleRate = cmd.getSingleRate();
        BigDecimal amountLimitMin = cmd.getAmountLimitMin();
        BigDecimal amountLimitMax = cmd.getAmountLimitMax();
        String settleType = cmd.getSettleType();
        String settleTime = cmd.getSettleTime();

        if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
            QueryWrapper<MerchantPaymentChannelConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(MerchantPaymentChannelConfig::getMerchantId, merchantId)
                    .eq(MerchantPaymentChannelConfig::getPaymentMethod, paymentMethod)
                    .eq(StringUtils.isNotBlank(channelCode), MerchantPaymentChannelConfig::getChannelCode, channelCode);
            List<MerchantPaymentChannelConfig> configList = MerchantPaymentChannelConfigService.list(queryWrapper);

            if (CollectionUtils.isNotEmpty(configList)) {
                List<Long> collect = configList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                UpdateWrapper<MerchantPaymentChannelConfig> payConfigUpdate = new UpdateWrapper<>();
                payConfigUpdate.lambda()
                        .set(Objects.nonNull(singleFee), MerchantPaymentChannelConfig::getSingleFee, singleFee)
                        .set(Objects.nonNull(singleRate), MerchantPaymentChannelConfig::getSingleRate, singleRate)
                        .set(Objects.nonNull(amountLimitMin), MerchantPaymentChannelConfig::getAmountLimitMin, amountLimitMin)
                        .set(Objects.nonNull(amountLimitMax), MerchantPaymentChannelConfig::getAmountLimitMax, amountLimitMax)
                        .set(StringUtils.isNotBlank(settleType), MerchantPaymentChannelConfig::getSettleType, settleType)
                        .set(StringUtils.isNotBlank(settleTime), MerchantPaymentChannelConfig::getSettleTime, settleTime)
                        .in(MerchantPaymentChannelConfig::getId, collect);
                return MerchantPaymentChannelConfigService.update(payConfigUpdate);
            }

        } else if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {
            QueryWrapper<MerchantPayoutChannelConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(MerchantPayoutChannelConfig::getMerchantId, merchantId)
                    .eq(MerchantPayoutChannelConfig::getPaymentMethod, paymentMethod)
                    .eq(StringUtils.isNotBlank(channelCode), MerchantPayoutChannelConfig::getChannelCode, channelCode);
            List<MerchantPayoutChannelConfig> configList = MerchantPayoutChannelConfigService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(configList)) {
                List<Long> collect = configList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                UpdateWrapper<MerchantPayoutChannelConfig> cashConfigUpdate = new UpdateWrapper<>();
                cashConfigUpdate.lambda()
                        .set(Objects.nonNull(singleFee), MerchantPayoutChannelConfig::getSingleFee, singleFee)
                        .set(Objects.nonNull(singleRate), MerchantPayoutChannelConfig::getSingleRate, singleRate)
                        .set(Objects.nonNull(amountLimitMin), MerchantPayoutChannelConfig::getAmountLimitMin, amountLimitMin)
                        .set(Objects.nonNull(amountLimitMax), MerchantPayoutChannelConfig::getAmountLimitMax, amountLimitMax)
                        .set(StringUtils.isNotBlank(settleType), MerchantPayoutChannelConfig::getSettleType, settleType)
                        .set(StringUtils.isNotBlank(settleTime), MerchantPayoutChannelConfig::getSettleTime, settleTime)
                        .in(MerchantPayoutChannelConfig::getId, collect);
                return MerchantPayoutChannelConfigService.update(cashConfigUpdate);
            }
        } else {
            throw new PaymentException(ExceptionCode.UNSUPPORTED_TRADE_TYPE, tradeTypeEnum.name());
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncMerchantChannelConfig(MerchantIdCommand cmd) {
        log.info("syncMerchantChannelConfig cmd={}", JSONUtil.toJsonStr(cmd));
        String merchantId = cmd.getMerchantId();

        //查询商户
        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, merchantId).last(LIMIT_1);
        Merchant merchant = merchantService.getOne(merchantQuery);
        Assert.isNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_EXIST));

        /**
         * 收款
         */
        //默认模版
        QueryWrapper<MerchantTemplatePaymentChannelConfig> defaultPayQuery = new QueryWrapper<>();
        defaultPayQuery.lambda().eq(MerchantTemplatePaymentChannelConfig::getIndustryId, 0);
        List<MerchantTemplatePaymentChannelConfig> defaultPayTemplateList =
                merchantTemplatePaymentChannelConfigService.list(defaultPayQuery);

        if (CollectionUtils.isNotEmpty(defaultPayTemplateList)) {
            //查询商户配置
            QueryWrapper<MerchantPaymentChannelConfig> configQuery = new QueryWrapper<>();
            configQuery.lambda().eq(MerchantPaymentChannelConfig::getMerchantId, merchantId);
            List<MerchantPaymentChannelConfig> configList = MerchantPaymentChannelConfigService.list(configQuery);

            //分组
            Map<String, MerchantTemplatePaymentChannelConfig> defaultTemplateMap = defaultPayTemplateList.stream()
                    .collect(Collectors.toMap(e -> e.getPaymentMethod() + e.getChannelCode(), Function.identity()));
            Map<String, MerchantPaymentChannelConfig> configMap = configList.stream()
                    .collect(Collectors.toMap(e -> e.getPaymentMethod() + e.getChannelCode(), Function.identity()));
            log.info("syncMerchantChannelConfig defaultTemplateMap={}, configMap={}", defaultTemplateMap.size(),
                    configMap.size());

            //求差集
            Set<String> defaultKeySet = defaultTemplateMap.keySet();
            defaultKeySet.removeAll(configMap.keySet());
            log.info("syncMerchantChannelConfig defaultKeySet={}", defaultKeySet);

            //新增配置
            if (CollectionUtils.isNotEmpty(defaultKeySet)) {
                List<MerchantPaymentChannelConfig> collect = defaultKeySet.stream().map(e -> {
                    MerchantTemplatePaymentChannelConfig defaultTemplate = defaultTemplateMap.get(e);

                    //查询该商户是否已经存在该支付方式, 同一个支付方式费率必须相等
                    MerchantPaymentChannelConfig oldOne = configList.stream()
                            .filter(f -> f.getPaymentMethod().equals(defaultTemplate.getPaymentMethod()))
                            .findAny()
                            .orElse(null);

                    MerchantPaymentChannelConfig channelConfig = new MerchantPaymentChannelConfig();
                    if (Objects.nonNull(oldOne)) {
                        channelConfig.setMerchantId(merchant.getMerchantId());
                        channelConfig.setPaymentMethod(defaultTemplate.getPaymentMethod());
                        channelConfig.setChannelCode(defaultTemplate.getChannelCode());
                        channelConfig.setChannelName(defaultTemplate.getChannelName());
                        channelConfig.setPriority(99);
                        channelConfig.setSingleFee(oldOne.getSingleFee());
                        channelConfig.setSingleRate(oldOne.getSingleRate());
                        channelConfig.setAmountLimitMin(oldOne.getAmountLimitMin());
                        channelConfig.setAmountLimitMax(oldOne.getAmountLimitMax());
                        channelConfig.setSettleType(oldOne.getSettleType());
                        channelConfig.setSettleTime(oldOne.getSettleTime());
                        channelConfig.setStatus(defaultTemplate.isStatus());
                        channelConfig.setCreateTime(LocalDateTime.now());
                    } else {
                        channelConfig.setMerchantId(merchant.getMerchantId());
                        channelConfig.setPaymentMethod(defaultTemplate.getPaymentMethod());
                        channelConfig.setChannelCode(defaultTemplate.getChannelCode());
                        channelConfig.setChannelName(defaultTemplate.getChannelName());
                        channelConfig.setPriority(99);
                        channelConfig.setSingleFee(defaultTemplate.getSingleFee());
                        channelConfig.setSingleRate(defaultTemplate.getSingleRate());
                        channelConfig.setAmountLimitMin(defaultTemplate.getAmountLimitMin());
                        channelConfig.setAmountLimitMax(defaultTemplate.getAmountLimitMax());
                        channelConfig.setSettleType(defaultTemplate.getSettleType());
                        channelConfig.setSettleTime(defaultTemplate.getSettleTime());
                        channelConfig.setStatus(defaultTemplate.isStatus());
                        channelConfig.setCreateTime(LocalDateTime.now());
                    }
                    return channelConfig;
                }).collect(Collectors.toList());
                MerchantPaymentChannelConfigService.saveBatch(collect);
            }
        }

        /**
         * 代付
         */
        //默认模版
        QueryWrapper<MerchantTemplatePayoutChannelConfig> defaultCashQuery = new QueryWrapper<>();
        defaultCashQuery.lambda().eq(MerchantTemplatePayoutChannelConfig::getIndustryId, 0);
        List<MerchantTemplatePayoutChannelConfig> defaultCashTemplateList =
                merchantTemplatePayoutChannelConfigService.list(defaultCashQuery);
        if (CollectionUtils.isNotEmpty(defaultCashTemplateList)) {

            //查询商户配置
            QueryWrapper<MerchantPayoutChannelConfig> configQuery = new QueryWrapper<>();
            configQuery.lambda().eq(MerchantPayoutChannelConfig::getMerchantId, merchantId);
            List<MerchantPayoutChannelConfig> configList =
                    MerchantPayoutChannelConfigService.list(configQuery);

            //分组
            Map<String, MerchantTemplatePayoutChannelConfig> defaultTemplateMap = defaultCashTemplateList.stream()
                    .collect(Collectors.toMap(e -> e.getPaymentMethod() + e.getChannelCode(), Function.identity()));
            Map<String, MerchantPayoutChannelConfig> configMap = configList.stream()
                    .collect(Collectors.toMap(e -> e.getPaymentMethod() + e.getChannelCode(), Function.identity()));
            log.info("syncMerchantChannelConfig defaultCashTemplateList={}, configMap={}",
                    defaultCashTemplateList.size(), configMap.size());

            //求差集
            Set<String> defaultKeySet = defaultTemplateMap.keySet();
            defaultKeySet.removeAll(configMap.keySet());
            log.info("syncMerchantChannelConfig defaultKeySet={}", defaultKeySet);

            if (CollectionUtils.isNotEmpty(defaultKeySet)) {
                List<MerchantPayoutChannelConfig> collect = defaultKeySet.stream().map(e -> {
                    MerchantTemplatePayoutChannelConfig defaultTemplate = defaultTemplateMap.get(e);

                    MerchantPayoutChannelConfig oldOne = configList.stream()
                            .filter(f -> f.getPaymentMethod().equals(defaultTemplate.getPaymentMethod()))
                            .findAny()
                            .orElse(null);

                    MerchantPayoutChannelConfig channelConfig = new MerchantPayoutChannelConfig();
                    if (Objects.nonNull(oldOne)) {
                        channelConfig.setMerchantId(merchant.getMerchantId());
                        channelConfig.setPaymentMethod(defaultTemplate.getPaymentMethod());
                        channelConfig.setChannelCode(defaultTemplate.getChannelCode());
                        channelConfig.setChannelName(defaultTemplate.getChannelName());
                        channelConfig.setPriority(99);
                        channelConfig.setSingleFee(oldOne.getSingleFee());
                        channelConfig.setSingleRate(oldOne.getSingleRate());
                        channelConfig.setAmountLimitMin(oldOne.getAmountLimitMin());
                        channelConfig.setAmountLimitMax(oldOne.getAmountLimitMax());
                        channelConfig.setSettleType(oldOne.getSettleType());
                        channelConfig.setSettleTime(oldOne.getSettleTime());
                        channelConfig.setStatus(defaultTemplate.isStatus());
                        channelConfig.setCreateTime(LocalDateTime.now());
                    } else {
                        channelConfig.setMerchantId(merchant.getMerchantId());
                        channelConfig.setPaymentMethod(defaultTemplate.getPaymentMethod());
                        channelConfig.setChannelCode(defaultTemplate.getChannelCode());
                        channelConfig.setChannelName(defaultTemplate.getChannelName());
                        channelConfig.setPriority(99);
                        channelConfig.setSingleFee(defaultTemplate.getSingleFee());
                        channelConfig.setSingleRate(defaultTemplate.getSingleRate());
                        channelConfig.setAmountLimitMin(defaultTemplate.getAmountLimitMin());
                        channelConfig.setAmountLimitMax(defaultTemplate.getAmountLimitMax());
                        channelConfig.setSettleType(defaultTemplate.getSettleType());
                        channelConfig.setSettleTime(defaultTemplate.getSettleTime());
                        channelConfig.setStatus(defaultTemplate.isStatus());
                        channelConfig.setCreateTime(LocalDateTime.now());
                    }
                    return channelConfig;
                }).collect(Collectors.toList());
                MerchantPayoutChannelConfigService.saveBatch(collect);
            }
        }
        return true;
    }


}
