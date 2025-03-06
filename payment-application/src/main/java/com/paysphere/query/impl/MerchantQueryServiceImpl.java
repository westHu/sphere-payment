package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.db.entity.MerchantOperator;
import com.paysphere.db.entity.MerchantPaymentChannelConfig;
import com.paysphere.db.entity.MerchantPaymentConfig;
import com.paysphere.db.entity.MerchantPayoutChannelConfig;
import com.paysphere.db.entity.MerchantPayoutConfig;
import com.paysphere.db.entity.MerchantWithdrawChannelConfig;
import com.paysphere.db.entity.MerchantWithdrawConfig;
import com.paysphere.enums.MerchantQueryTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.query.MerchantQueryService;
import com.paysphere.query.dto.MerchantDropDTO;
import com.paysphere.query.dto.MerchantTradeDTO;
import com.paysphere.query.param.MerchantDropListParam;
import com.paysphere.query.param.MerchantIdParam;
import com.paysphere.query.param.MerchantPageParam;
import com.paysphere.query.param.OptionalMerchantDetailParam;
import com.paysphere.repository.MerchantConfigService;
import com.paysphere.repository.MerchantOperatorService;
import com.paysphere.repository.MerchantPaymentChannelConfigService;
import com.paysphere.repository.MerchantPaymentConfigService;
import com.paysphere.repository.MerchantPayoutChannelConfigService;
import com.paysphere.repository.MerchantPayoutConfigService;
import com.paysphere.repository.MerchantService;
import com.paysphere.repository.MerchantWithdrawChannelConfigService;
import com.paysphere.repository.MerchantWithdrawConfigService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantQueryServiceImpl implements MerchantQueryService {

    @Resource
    MerchantService merchantService;
    @Resource
    MerchantConfigService merchantConfigService;
    @Resource
    MerchantPaymentConfigService merchantPaymentConfigService;
    @Resource
    MerchantPayoutConfigService merchantPayoutConfigService;
    @Resource
    MerchantWithdrawConfigService merchantWithdrawConfigService;
    @Resource
    MerchantPaymentChannelConfigService merchantPaymentChannelConfigService;
    @Resource
    MerchantPayoutChannelConfigService merchantPayoutChannelConfigService;
    @Resource
    MerchantWithdrawChannelConfigService merchantWithdrawChannelConfigService;
    @Resource
    MerchantOperatorService merchantOperatorService;


    @Override
    public List<MerchantDropDTO> dropMerchantList(MerchantDropListParam param) {
        QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(Merchant::getMerchantId);
        List<Merchant> merchantList = merchantService.list(queryWrapper);

        return merchantList.stream().map(e -> {
            MerchantDropDTO dropDTO = new MerchantDropDTO();
            dropDTO.setMerchantId(e.getMerchantId());
            dropDTO.setMerchantName(e.getMerchantName());
            return dropDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<Merchant> pageBaseMerchantList(MerchantPageParam param) {
        return null;
    }

    @Override
    public Merchant getBaseMerchant(MerchantIdParam param) {
        return null;
    }

    @Override
    public Merchant getMerchant(String merchantId) {
        log.info("getMerchant param={}", merchantId);

        QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Merchant::getMerchantId, merchantId)

                .orderByAsc(Merchant::getMerchantId);
        return merchantService.getOne(queryWrapper);
    }

    /**
     * 商户交易信息
     */
    @SneakyThrows
    @Override
    public MerchantTradeDTO getMerchantTradeDTO(OptionalMerchantDetailParam param) {
        log.info("getMerchantTradeDTO param={}", JSONUtil.toJsonStr(param));
        List<MerchantQueryTypeEnum> typeList = param.getTypeList();
        String merchantId = param.getMerchantId();
        String paymentMethod = param.getPaymentMethod();
        BigDecimal amount = param.getAmount();
        Integer area = param.getArea();

        //查询商户的信息和配置
        MerchantTradeDTO merchantTradeDTO = new MerchantTradeDTO();
        CompletableFuture<Merchant> baseFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantConfig> configFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantPaymentConfig> paymentConfigFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantPaymentChannelConfig> paymentChannelConfig = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantPayoutConfig> payoutConfigFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantPayoutChannelConfig> payoutChannelConfigFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantWithdrawConfig> withdrawConfigFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantWithdrawChannelConfig> withdrawPaymentConfigFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<List<MerchantOperator>> operatorFuture = CompletableFuture.supplyAsync(() -> null);

        //merchant base info
        if (typeList.contains(MerchantQueryTypeEnum.BASE)) {
            baseFuture = CompletableFuture.supplyAsync(() -> getMerchant(merchantId));
        }

        //merchant config
        if (typeList.contains(MerchantQueryTypeEnum.CONFIG)) {
            configFuture = CompletableFuture.supplyAsync(() -> getMerchantConfig(merchantId));
        }

        //merchant payment config
        if (typeList.contains(MerchantQueryTypeEnum.PAYMENT_CONFIG)) {
            paymentConfigFuture = CompletableFuture.supplyAsync(() -> getMerchantPaymentConfig(merchantId));
        }

        //merchant payment channel config
        if (typeList.contains(MerchantQueryTypeEnum.PAYMENT_CHANNEL_CONFIG)) {
            paymentChannelConfig = CompletableFuture.supplyAsync(() -> getMerchantPaymentChannelConfig(merchantId, amount, paymentMethod, area));
        }

        //merchant payout config
        if (typeList.contains(MerchantQueryTypeEnum.PAYOUT_CONFIG)) {
            payoutConfigFuture = CompletableFuture.supplyAsync(() -> getMerchantPayoutConfig(merchantId));
        }

        //merchant payout payment config
        if (typeList.contains(MerchantQueryTypeEnum.PAYOUT_CHANNEL_CONFIG)) {
            payoutChannelConfigFuture = CompletableFuture.supplyAsync(() -> getMerchantPayoutChannelConfig(merchantId, amount, paymentMethod, area));
        }

        //merchant withdraw config
        if (typeList.contains(MerchantQueryTypeEnum.WITHDRAW_CONFIG)) {
            withdrawConfigFuture = CompletableFuture.supplyAsync(() -> getMerchantWithdrawConfig(merchantId));
        }

        //merchant withdraw payment config
        if (typeList.contains(MerchantQueryTypeEnum.WITHDRAW_CHANNEL_CONFIG)) {
            withdrawPaymentConfigFuture = CompletableFuture.supplyAsync(() -> getMerchantWithdrawChannelConfig(merchantId, amount, paymentMethod, area));
        }

        //merchant operator
        if (typeList.contains(MerchantQueryTypeEnum.ACCOUNT)) {
            operatorFuture = CompletableFuture.supplyAsync(() -> getMerchantOperatorList(merchantId));
        }

        //merchant operator
        if (typeList.contains(MerchantQueryTypeEnum.OPERATOR)) {
            operatorFuture = CompletableFuture.supplyAsync(() -> getMerchantOperatorList(merchantId));
        }

        // join & build
        CompletableFuture.allOf(baseFuture, configFuture,
                paymentConfigFuture, paymentChannelConfig,
                payoutConfigFuture, payoutChannelConfigFuture,
                withdrawConfigFuture, withdrawPaymentConfigFuture,
                operatorFuture).join();

        merchantTradeDTO.setMerchant(baseFuture.get());
        merchantTradeDTO.setMerchantConfig(configFuture.get());

        merchantTradeDTO.setMerchantPaymentConfig(paymentConfigFuture.get());
        merchantTradeDTO.setMerchantPaymentChannelConfig(paymentChannelConfig.get());

        merchantTradeDTO.setMerchantPayoutConfig(payoutConfigFuture.get());
        merchantTradeDTO.setMerchantPayoutChannelConfig(payoutChannelConfigFuture.get());

        merchantTradeDTO.setMerchantWithdrawConfig(withdrawConfigFuture.get());
        merchantTradeDTO.setMerchantWithdrawChannelConfig(withdrawPaymentConfigFuture.get());

        return merchantTradeDTO;
    }

    //-----------------------------------------------------------------------------------------------------------------

    /**
     * 商户配置
     */
    private MerchantConfig getMerchantConfig(String merchantId) {
        QueryWrapper<MerchantConfig> query = new QueryWrapper<>();
        query.lambda().eq(MerchantConfig::getMerchantId, merchantId).last(LIMIT_1);
        return merchantConfigService.getOne(query);
    }

    /**
     * 商户支付配置
     */
    private MerchantPaymentConfig getMerchantPaymentConfig(String merchantId) {
        QueryWrapper<MerchantPaymentConfig> query = new QueryWrapper<>();
        query.lambda().eq(MerchantPaymentConfig::getMerchantId, merchantId).last(LIMIT_1);
        return merchantPaymentConfigService.getOne(query);
    }

    /**
     * 商户出款配置
     */
    private MerchantPayoutConfig getMerchantPayoutConfig(String merchantId) {
        QueryWrapper<MerchantPayoutConfig> query = new QueryWrapper<>();
        query.lambda().eq(MerchantPayoutConfig::getMerchantId, merchantId).last(LIMIT_1);
        return merchantPayoutConfigService.getOne(query);
    }

    /**
     * 商户提现配置
     */
    private MerchantWithdrawConfig getMerchantWithdrawConfig(String merchantId) {
        QueryWrapper<MerchantWithdrawConfig> query = new QueryWrapper<>();
        query.lambda().eq(MerchantWithdrawConfig::getMerchantId, merchantId).last(LIMIT_1);
        return merchantWithdrawConfigService.getOne(query);
    }

    /**
     * 商户操作员
     */
    private List<MerchantOperator> getMerchantOperatorList(String merchantId) {
        QueryWrapper<MerchantOperator> query = new QueryWrapper<>();
        query.lambda().eq(MerchantOperator::getMerchantId, merchantId);
        return merchantOperatorService.list(query);
    }



    /**
     * 收款配置
     */
    private MerchantPaymentChannelConfig getMerchantPaymentChannelConfig(String merchantId,
                                                                         BigDecimal amount,

                                                                         String paymentMethod, Integer area) {
        QueryWrapper<MerchantPaymentChannelConfig> paymentConfigQuery = new QueryWrapper<>();
        paymentConfigQuery.lambda().eq(MerchantPaymentChannelConfig::getMerchantId, merchantId)
                .eq(MerchantPaymentChannelConfig::getPaymentMethod, paymentMethod)
                .eq(MerchantPaymentChannelConfig::getArea, area)
                .eq(MerchantPaymentChannelConfig::isStatus, true)
                .orderByAsc(MerchantPaymentChannelConfig::getPriority);
        List<MerchantPaymentChannelConfig> configList = merchantPaymentChannelConfigService.list(paymentConfigQuery);
        if (CollectionUtils.isEmpty(configList)) {
            log.error("getMerchantPaymentChannelConfig paymentConfig is empty. {}, {}", merchantId, paymentMethod);
            throw new PaymentException(ExceptionCode.PAYMENT_METHOD_NOT_ACTIVE, merchantId, paymentMethod);
        }

        //如果传入金额则对金额进行过滤
        if (Objects.nonNull(amount)) {
            configList = configList.stream()
                    .filter(e -> Objects.nonNull(e.getAmountLimitMin()) && amount.compareTo(e.getAmountLimitMin()) >= 0)
                    .filter(e -> Objects.nonNull(e.getAmountLimitMax()) && amount.compareTo(e.getAmountLimitMax()) <= 0)
                    .collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(configList)) {
            log.error("getMerchantPaymentChannelConfig paymentConfig amount limit. {}, {}", merchantId, paymentMethod);
            throw new PaymentException(ExceptionCode.PAYMENT_METHOD_AMOUNT_LIMIT, merchantId, paymentMethod);
        }

        //目前就取第一条
        return configList.get(0);
    }

    /**
     * 代付配置
     */
    private MerchantPayoutChannelConfig getMerchantPayoutChannelConfig(String merchantId,
                                                                       BigDecimal amount,
                                                                               String paymentMethod,  Integer area) {
        QueryWrapper<MerchantPayoutChannelConfig> cashConfigQuery = new QueryWrapper<>();
        cashConfigQuery.lambda().eq(MerchantPayoutChannelConfig::getMerchantId, merchantId)
                .eq(MerchantPayoutChannelConfig::getPaymentMethod, paymentMethod)
                .eq(MerchantPayoutChannelConfig::getArea, area)
                .eq(MerchantPayoutChannelConfig::isStatus, true)
                .orderByAsc(MerchantPayoutChannelConfig::getPriority);
        List<MerchantPayoutChannelConfig> configList = merchantPayoutChannelConfigService.list(cashConfigQuery);
        if (CollectionUtils.isEmpty(configList)) {
            log.error("getMerchantTradeDTO cash paymentCashConfig is empty. {}, {}", merchantId, paymentMethod);
            throw new PaymentException(ExceptionCode.PAYMENT_METHOD_NOT_ACTIVE, merchantId, paymentMethod);
        }

        //如果传入金额则对金额进行过滤
        if (Objects.nonNull(amount)) {
            configList = configList.stream()
                    .filter(e -> Objects.nonNull(e.getAmountLimitMin()) && amount.compareTo(e.getAmountLimitMin()) >= 0)
                    .filter(e -> Objects.nonNull(e.getAmountLimitMax()) && amount.compareTo(e.getAmountLimitMax()) <= 0)
                    .collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(configList)) {
            log.error("getMerchantTradeDTO cash paymentMethod amount limit. {}, {}", merchantId, paymentMethod);
            throw new PaymentException(ExceptionCode.PAYMENT_METHOD_AMOUNT_LIMIT, merchantId, paymentMethod);
        }
        return configList.get(0);
    }

    /**
     * 提现配置
     */
    private MerchantWithdrawChannelConfig getMerchantWithdrawChannelConfig(String merchantId,
                                                                                     BigDecimal amount,

                                                                           String paymentMethod, Integer area) {
        QueryWrapper<MerchantWithdrawChannelConfig> withdrawConfigQuery = new QueryWrapper<>();
        withdrawConfigQuery.lambda().eq(MerchantWithdrawChannelConfig::getMerchantId, merchantId)
                .eq(MerchantWithdrawChannelConfig::getPaymentMethod, paymentMethod)
                .eq(MerchantWithdrawChannelConfig::getArea, area)
                .eq(MerchantWithdrawChannelConfig::isStatus, true)
                .orderByAsc(MerchantWithdrawChannelConfig::getPriority);
        List<MerchantWithdrawChannelConfig> configList = merchantWithdrawChannelConfigService.list(withdrawConfigQuery);
        if (CollectionUtils.isEmpty(configList)) {
            log.error("getMerchantWithdrawChannelConfig withdraw paymentCashConfig is empty. {}, {}", merchantId, paymentMethod);
            throw new PaymentException(ExceptionCode.PAYMENT_METHOD_NOT_ACTIVE, merchantId, paymentMethod);
        }

        //如果传入金额则对金额进行过滤
        if (Objects.nonNull(amount)) {
            configList = configList.stream()
                    .filter(e -> Objects.nonNull(e.getAmountLimitMin()) && amount.compareTo(e.getAmountLimitMin()) >= 0)
                    .filter(e -> Objects.nonNull(e.getAmountLimitMax()) && amount.compareTo(e.getAmountLimitMax()) <= 0)
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(configList)) {
            log.error("getMerchantTradeDTO withdraw paymentMethod amount limit. {}, {}", merchantId, paymentMethod);
            throw new PaymentException(ExceptionCode.PAYMENT_METHOD_AMOUNT_LIMIT, merchantId, paymentMethod);
        }
        return configList.get(0);
    }

}
