package app.sphere.query.impl;

import app.sphere.query.param.MerchantDropListParam;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantConfig;
import infrastructure.sphere.db.entity.MerchantPaymentChannelConfig;
import infrastructure.sphere.db.entity.MerchantPaymentConfig;
import infrastructure.sphere.db.entity.MerchantPayoutChannelConfig;
import infrastructure.sphere.db.entity.MerchantPayoutConfig;
import infrastructure.sphere.db.entity.MerchantWithdrawChannelConfig;
import infrastructure.sphere.db.entity.MerchantWithdrawConfig;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import app.sphere.query.MerchantQueryService;
import app.sphere.query.dto.MerchantDropDTO;
import app.sphere.query.dto.MerchantTradeDTO;
import app.sphere.query.param.MerchantIdParam;
import app.sphere.query.param.MerchantPageParam;
import app.sphere.query.param.MerchantTradeParam;
import domain.sphere.repository.MerchantConfigRepository;
import domain.sphere.repository.MerchantPaymentChannelConfigRepository;
import domain.sphere.repository.MerchantPaymentConfigRepository;
import domain.sphere.repository.MerchantPayoutChannelConfigRepository;
import domain.sphere.repository.MerchantPayoutConfigRepository;
import domain.sphere.repository.MerchantRepository;
import domain.sphere.repository.MerchantWithdrawChannelConfigRepository;
import domain.sphere.repository.MerchantWithdrawConfigRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static share.sphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantQueryServiceImpl implements MerchantQueryService {

    @Resource
    MerchantRepository merchantRepository;
    @Resource
    MerchantConfigRepository merchantConfigRepository;
    @Resource
    MerchantPaymentConfigRepository merchantPaymentConfigRepository;
    @Resource
    MerchantPayoutConfigRepository merchantPayoutConfigRepository;
    @Resource
    MerchantWithdrawConfigRepository merchantWithdrawConfigRepository;
    @Resource
    MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository;
    @Resource
    MerchantPayoutChannelConfigRepository merchantPayoutChannelConfigRepository;
    @Resource
    MerchantWithdrawChannelConfigRepository merchantWithdrawChannelConfigRepository;

    @Override
    public List<MerchantDropDTO> dropMerchantList(MerchantDropListParam param) {
        QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(Merchant::getMerchantId);
        List<Merchant> merchantList = merchantRepository.list(queryWrapper);

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
    public Merchant getMerchant(MerchantIdParam param) {
        return null;
    }

    @SneakyThrows
    @Override
    public MerchantTradeDTO getMerchantTradeDTO(MerchantTradeParam param) {
        log.info("getMerchantTradeDTO param={}", JSONUtil.toJsonStr(param));
        TradeTypeEnum tradeTypeEnum = param.getTradeTypeEnum();
        String merchantId = param.getMerchantId();
        String paymentMethod = param.getPaymentMethod();
        BigDecimal amount = param.getAmount();
        String region = param.getRegion();

        //查询商户的信息和配置
        MerchantTradeDTO merchantTradeDTO = new MerchantTradeDTO();
        CompletableFuture<Merchant> baseFuture;
        CompletableFuture<MerchantConfig> configFuture;
        CompletableFuture<MerchantPaymentConfig> paymentConfigFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantPaymentChannelConfig> paymentChannelConfig = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantPayoutConfig> payoutConfigFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantPayoutChannelConfig> payoutChannelConfigFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantWithdrawConfig> withdrawConfigFuture = CompletableFuture.supplyAsync(() -> null);
        CompletableFuture<MerchantWithdrawChannelConfig> withdrawPaymentConfigFuture = CompletableFuture.supplyAsync(() -> null);

        //merchant base info
        baseFuture = CompletableFuture.supplyAsync(() -> getMerchant(merchantId));

        //merchant config
        configFuture = CompletableFuture.supplyAsync(() -> getMerchantConfig(merchantId));

        //merchant payment config
        if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
            paymentConfigFuture = CompletableFuture.supplyAsync(() -> getMerchantPaymentConfig(merchantId));
            paymentChannelConfig = CompletableFuture.supplyAsync(() -> getMerchantPaymentChannelConfig(merchantId, amount, paymentMethod, region));

        }

        //merchant payout config
        if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {
            payoutConfigFuture = CompletableFuture.supplyAsync(() -> getMerchantPayoutConfig(merchantId));
            payoutChannelConfigFuture = CompletableFuture.supplyAsync(() -> getMerchantPayoutChannelConfig(merchantId, amount, paymentMethod, region));

        }

        //merchant withdraw config
        if (tradeTypeEnum.equals(TradeTypeEnum.WITHDRAW)) {
            withdrawConfigFuture = CompletableFuture.supplyAsync(() -> getMerchantWithdrawConfig(merchantId));
            withdrawPaymentConfigFuture = CompletableFuture.supplyAsync(() -> getMerchantWithdrawChannelConfig(merchantId, amount, paymentMethod, region));

        }

        // join & build
        CompletableFuture.allOf(baseFuture, configFuture,
                paymentConfigFuture, paymentChannelConfig,
                payoutConfigFuture, payoutChannelConfigFuture,
                withdrawConfigFuture, withdrawPaymentConfigFuture);

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
     * 商户信息
     */
    private Merchant getMerchant(String merchantId) {
        log.info("getMerchant param={}", merchantId);

        QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Merchant::getMerchantId, merchantId).last(LIMIT_1);
        return merchantRepository.getOne(queryWrapper);
    }

    /**
     * 商户配置
     */
    private MerchantConfig getMerchantConfig(String merchantId) {
        QueryWrapper<MerchantConfig> query = new QueryWrapper<>();
        query.lambda().eq(MerchantConfig::getMerchantId, merchantId).last(LIMIT_1);
        return merchantConfigRepository.getOne(query);
    }

    /**
     * 商户支付配置
     */
    private MerchantPaymentConfig getMerchantPaymentConfig(String merchantId) {
        QueryWrapper<MerchantPaymentConfig> query = new QueryWrapper<>();
        query.lambda().eq(MerchantPaymentConfig::getMerchantId, merchantId).last(LIMIT_1);
        return merchantPaymentConfigRepository.getOne(query);
    }

    /**
     * 商户出款配置
     */
    private MerchantPayoutConfig getMerchantPayoutConfig(String merchantId) {
        QueryWrapper<MerchantPayoutConfig> query = new QueryWrapper<>();
        query.lambda().eq(MerchantPayoutConfig::getMerchantId, merchantId).last(LIMIT_1);
        return merchantPayoutConfigRepository.getOne(query);
    }

    /**
     * 商户提现配置
     */
    private MerchantWithdrawConfig getMerchantWithdrawConfig(String merchantId) {
        QueryWrapper<MerchantWithdrawConfig> query = new QueryWrapper<>();
        query.lambda().eq(MerchantWithdrawConfig::getMerchantId, merchantId).last(LIMIT_1);
        return merchantWithdrawConfigRepository.getOne(query);
    }

    /**
     * 收款配置
     */
    private MerchantPaymentChannelConfig getMerchantPaymentChannelConfig(String merchantId,
                                                                         BigDecimal amount,
                                                                         String paymentMethod,
                                                                         String region) {
        QueryWrapper<MerchantPaymentChannelConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantPaymentChannelConfig::getMerchantId, merchantId)
                .eq(MerchantPaymentChannelConfig::getPaymentMethod, paymentMethod)
                .eq(MerchantPaymentChannelConfig::isStatus, true)
                .orderByAsc(MerchantPaymentChannelConfig::getPriority);
        List<MerchantPaymentChannelConfig> configList = merchantPaymentChannelConfigRepository.list(configQuery);
        if (CollectionUtils.isEmpty(configList)) {
            log.error("getMerchantPaymentChannelConfig paymentConfig is empty. {}, {}", merchantId, paymentMethod);
            throw new PaymentException(ExceptionCode.MERCHANT_CONFIG_NOT_EXIST, "支付渠道配置");
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
            throw new PaymentException(ExceptionCode.MERCHANT_CONFIG_ERROR, "支付金额超出限制");
        }

        return configList.get(0);
    }

    /**
     * 代付配置
     */
    private MerchantPayoutChannelConfig getMerchantPayoutChannelConfig(String merchantId,
                                                                       BigDecimal amount,
                                                                       String paymentMethod,
                                                                       String region) {
        QueryWrapper<MerchantPayoutChannelConfig> cashConfigQuery = new QueryWrapper<>();
        cashConfigQuery.lambda().eq(MerchantPayoutChannelConfig::getMerchantId, merchantId)
                .eq(MerchantPayoutChannelConfig::getPaymentMethod, paymentMethod)
                .eq(MerchantPayoutChannelConfig::isStatus, true)
                .orderByAsc(MerchantPayoutChannelConfig::getPriority);
        List<MerchantPayoutChannelConfig> configList = merchantPayoutChannelConfigRepository.list(cashConfigQuery);
        if (CollectionUtils.isEmpty(configList)) {
            log.error("getMerchantTradeDTO cash paymentCashConfig is empty. {}, {}", merchantId, paymentMethod);
            throw new PaymentException(ExceptionCode.MERCHANT_CONFIG_NOT_EXIST, "代付渠道配置");
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
            throw new PaymentException(ExceptionCode.MERCHANT_CONFIG_ERROR, "代付金额超出限制");
        }

        return configList.get(0);
    }

    /**
     * 提现配置
     */
    private MerchantWithdrawChannelConfig getMerchantWithdrawChannelConfig(String merchantId,
                                                                           BigDecimal amount,
                                                                           String paymentMethod,
                                                                           String region) {
        QueryWrapper<MerchantWithdrawChannelConfig> withdrawConfigQuery = new QueryWrapper<>();
        withdrawConfigQuery.lambda().eq(MerchantWithdrawChannelConfig::getMerchantId, merchantId)
                .eq(MerchantWithdrawChannelConfig::getPaymentMethod, paymentMethod)
                .eq(MerchantWithdrawChannelConfig::isStatus, true)
                .orderByAsc(MerchantWithdrawChannelConfig::getPriority);
        List<MerchantWithdrawChannelConfig> configList = merchantWithdrawChannelConfigRepository.list(withdrawConfigQuery);
        if (CollectionUtils.isEmpty(configList)) {
            log.error("getMerchantWithdrawChannelConfig withdraw paymentCashConfig is empty. {}, {}", merchantId, paymentMethod);
            throw new PaymentException(ExceptionCode.MERCHANT_CONFIG_NOT_EXIST, "提现渠道配置");
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
            throw new PaymentException(ExceptionCode.MERCHANT_CONFIG_ERROR, "提现金额超出限制");
        }
        return configList.get(0);
    }

}
