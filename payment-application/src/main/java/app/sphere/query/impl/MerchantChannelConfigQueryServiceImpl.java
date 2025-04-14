package app.sphere.query.impl;

import app.sphere.query.MerchantChannelConfigQueryService;
import app.sphere.query.dto.MerchantChannelConfigChannelDTO;
import app.sphere.query.dto.MerchantChannelConfigDTO;
import app.sphere.query.dto.MerchantChannelConfigListDTO;
import app.sphere.query.param.MerchantChannelConfigListParam;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import domain.sphere.repository.MerchantConfigRepository;
import domain.sphere.repository.MerchantPaymentChannelConfigRepository;
import domain.sphere.repository.MerchantPayoutChannelConfigRepository;
import infrastructure.sphere.db.entity.MerchantConfig;
import infrastructure.sphere.db.entity.MerchantPaymentChannelConfig;
import infrastructure.sphere.db.entity.MerchantPayoutChannelConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import share.sphere.enums.QuerySourceEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static share.sphere.TradeConstant.LIMIT_1;

/**
 * 商户渠道配置查询服务实现类
 */
@Slf4j
@Service
public class MerchantChannelConfigQueryServiceImpl implements MerchantChannelConfigQueryService {

    @Resource
    MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository;
    @Resource
    MerchantPayoutChannelConfigRepository merchantPayoutChannelConfigRepository;
    @Resource
    MerchantConfigRepository merchantConfigRepository;

    @Override
    public MerchantChannelConfigListDTO getMerchantChannelConfigList(MerchantChannelConfigListParam param) {
        log.info("开始查询商户渠道配置列表, param={}", JSONUtil.toJsonStr(param));

        // 1. 参数校验和初始化
        String merchantId = param.getMerchantId();
        validateMerchantConfig(merchantId);
        QuerySourceEnum sourceEnum = QuerySourceEnum.codeToEnum(param.getQuerySource());
        log.info("商户渠道配置查询来源: merchantId={}, sourceEnum={}", merchantId, sourceEnum);

        // 2. 并行查询配置
        log.info("开始并行查询商户渠道配置, merchantId={}", merchantId);
        CompletableFuture<List<MerchantPaymentChannelConfig>> paymentConfigFuture = 
            CompletableFuture.supplyAsync(() -> queryPaymentConfigs(merchantId));
        CompletableFuture<List<MerchantPayoutChannelConfig>> payoutConfigFuture = 
            CompletableFuture.supplyAsync(() -> queryPayoutConfigs(merchantId));

        // 3. 等待结果并处理
        try {
            List<MerchantPaymentChannelConfig> paymentConfigs = paymentConfigFuture.get();
            List<MerchantPayoutChannelConfig> payoutConfigs = payoutConfigFuture.get();
            log.info("商户渠道配置查询完成, merchantId={}, paymentConfigs={}, payoutConfigs={}", 
                    merchantId, 
                    paymentConfigs != null ? paymentConfigs.size() : 0,
                    payoutConfigs != null ? payoutConfigs.size() : 0);

            MerchantChannelConfigListDTO result = new MerchantChannelConfigListDTO();
            if (sourceEnum == QuerySourceEnum.MERCHANT_ADMIN) {
                log.info("开始构建商户平台配置, merchantId={}", merchantId);
                result.setPaymentChannelConfigList(buildMerchantPaymentConfigs(paymentConfigs));
                result.setPayoutChannelConfigList(buildMerchantPayoutConfigs(payoutConfigs));
            } else {
                log.info("开始构建管理平台配置, merchantId={}", merchantId);
                result.setPaymentChannelConfigList(buildAdminPaymentConfigs(paymentConfigs));
                result.setPayoutChannelConfigList(buildAdminPayoutConfigs(payoutConfigs));
            }
            log.info("商户渠道配置列表构建完成, merchantId={}, result={}", merchantId, JSONUtil.toJsonStr(result));
            return result;
        } catch (Exception e) {
            log.error("查询商户渠道配置失败, merchantId={}, error={}", merchantId, e.getMessage(), e);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR);
        }
    }

    /**
     * 验证商户配置
     */
    private void validateMerchantConfig(String merchantId) {
        log.info("开始验证商户配置, merchantId={}", merchantId);
        QueryWrapper<MerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantConfig::getMerchantId, merchantId).last(LIMIT_1);
        MerchantConfig merchantConfig = merchantConfigRepository.getOne(configQuery);
        Assert.notNull(merchantConfig, () -> new PaymentException(ExceptionCode.MERCHANT_CONFIG_NOT_EXIST, merchantId));
        log.info("商户配置验证通过, merchantId={}", merchantId);
    }

    /**
     * 查询收款配置
     */
    private List<MerchantPaymentChannelConfig> queryPaymentConfigs(String merchantId) {
        log.info("开始查询商户收款配置, merchantId={}", merchantId);
        QueryWrapper<MerchantPaymentChannelConfig> query = new QueryWrapper<>();
        query.lambda().eq(MerchantPaymentChannelConfig::getMerchantId, merchantId);
        List<MerchantPaymentChannelConfig> configs = merchantPaymentChannelConfigRepository.list(query);
        log.info("商户收款配置查询完成, merchantId={}, configs={}", merchantId, configs != null ? configs.size() : 0);
        return configs;
    }

    /**
     * 查询代付配置
     */
    private List<MerchantPayoutChannelConfig> queryPayoutConfigs(String merchantId) {
        log.info("开始查询商户代付配置, merchantId={}", merchantId);
        QueryWrapper<MerchantPayoutChannelConfig> query = new QueryWrapper<>();
        query.lambda().eq(MerchantPayoutChannelConfig::getMerchantId, merchantId);
        List<MerchantPayoutChannelConfig> configs = merchantPayoutChannelConfigRepository.list(query);
        log.info("商户代付配置查询完成, merchantId={}, configs={}", merchantId, configs != null ? configs.size() : 0);
        return configs;
    }

    /**
     * 构建商户平台收款配置
     */
    private List<MerchantChannelConfigDTO> buildMerchantPaymentConfigs(List<MerchantPaymentChannelConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            log.info("商户平台收款配置为空");
            return null;
        }

        Map<String, List<MerchantPaymentChannelConfig>> groupMap = configs.stream()
                .collect(Collectors.groupingBy(MerchantPaymentChannelConfig::getPaymentMethod));
        log.info("商户平台收款配置分组完成, groupMap={}", groupMap.size());

        List<MerchantChannelConfigDTO> result = groupMap.entrySet().stream()
                .map(e -> buildMerchantPaymentConfigDTO(e.getKey(), e.getValue()))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MerchantChannelConfigDTO::getPaymentMethod))
                .collect(Collectors.toList());
        log.info("商户平台收款配置构建完成, result={}", result.size());
        return result;
    }

    /**
     * 构建商户平台代付配置
     */
    private List<MerchantChannelConfigDTO> buildMerchantPayoutConfigs(List<MerchantPayoutChannelConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            log.info("商户平台代付配置为空");
            return null;
        }

        Map<String, List<MerchantPayoutChannelConfig>> groupMap = configs.stream()
                .collect(Collectors.groupingBy(MerchantPayoutChannelConfig::getPaymentMethod));
        log.info("商户平台代付配置分组完成, groupMap={}", groupMap.size());

        List<MerchantChannelConfigDTO> result = groupMap.entrySet().stream()
                .map(e -> buildMerchantPayoutConfigDTO(e.getKey(), e.getValue()))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MerchantChannelConfigDTO::getPaymentMethod))
                .collect(Collectors.toList());
        log.info("商户平台代付配置构建完成, result={}", result.size());
        return result;
    }

    /**
     * 构建商户平台收款配置DTO
     */
    private MerchantChannelConfigDTO buildMerchantPaymentConfigDTO(String paymentMethod, List<MerchantPaymentChannelConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            log.info("商户平台收款配置DTO构建失败, paymentMethod={}, configs为空", paymentMethod);
            return null;
        }

        MerchantPaymentChannelConfig config = configs.get(0);
        MerchantChannelConfigDTO dto = new MerchantChannelConfigDTO();
        dto.setPaymentMethod(paymentMethod);
        dto.setSingleRate(config.getSingleRate());
        dto.setSingleFee(config.getSingleFee());
        dto.setAmountLimitMin(config.getAmountLimitMin());
        dto.setAmountLimitMax(config.getAmountLimitMax());
        dto.setSettleType(config.getSettleType());
        dto.setSettleTime(config.getSettleTime());
        dto.setStatus(configs.stream().anyMatch(MerchantPaymentChannelConfig::isStatus));

        List<MerchantChannelConfigChannelDTO> channelList = configs.stream()
                .map(this::buildChannelDTO)
                .collect(Collectors.toSet())
                .stream()
                .sorted(Comparator.comparing(MerchantChannelConfigChannelDTO::getPriority))
                .collect(Collectors.toList());
        dto.setChannelList(channelList);
        log.info("商户平台收款配置DTO构建完成, paymentMethod={}, channelList={}", paymentMethod, channelList.size());
        return dto;
    }

    /**
     * 构建商户平台代付配置DTO
     */
    private MerchantChannelConfigDTO buildMerchantPayoutConfigDTO(String paymentMethod, List<MerchantPayoutChannelConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            log.info("商户平台代付配置DTO构建失败, paymentMethod={}, configs为空", paymentMethod);
            return null;
        }

        MerchantPayoutChannelConfig config = configs.get(0);
        MerchantChannelConfigDTO dto = new MerchantChannelConfigDTO();
        dto.setPaymentMethod(paymentMethod);
        dto.setSingleRate(config.getSingleRate());
        dto.setSingleFee(config.getSingleFee());
        dto.setAmountLimitMin(config.getAmountLimitMin());
        dto.setAmountLimitMax(config.getAmountLimitMax());
        dto.setSettleType(config.getSettleType());
        dto.setSettleTime(config.getSettleTime());
        dto.setStatus(configs.stream().anyMatch(MerchantPayoutChannelConfig::isStatus));

        List<MerchantChannelConfigChannelDTO> channelList = configs.stream()
                .map(this::buildChannelDTO)
                .collect(Collectors.toSet())
                .stream()
                .sorted(Comparator.comparing(MerchantChannelConfigChannelDTO::getPriority))
                .collect(Collectors.toList());
        dto.setChannelList(channelList);
        log.info("商户平台代付配置DTO构建完成, paymentMethod={}, channelList={}", paymentMethod, channelList.size());
        return dto;
    }

    /**
     * 构建管理平台收款配置
     */
    private List<MerchantChannelConfigDTO> buildAdminPaymentConfigs(List<MerchantPaymentChannelConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            log.info("管理平台收款配置为空");
            return null;
        }

        Map<String, List<MerchantPaymentChannelConfig>> groupMap = configs.stream()
                .collect(Collectors.groupingBy(MerchantPaymentChannelConfig::getPaymentMethod));
        log.info("管理平台收款配置分组完成, groupMap={}", groupMap.size());

        List<MerchantChannelConfigDTO> result = groupMap.entrySet().stream()
                .map(e -> buildAdminPaymentConfigDTO(e.getKey(), e.getValue()))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MerchantChannelConfigDTO::getPaymentMethod))
                .collect(Collectors.toList());
        log.info("管理平台收款配置构建完成, result={}", result.size());
        return result;
    }

    /**
     * 构建管理平台代付配置
     */
    private List<MerchantChannelConfigDTO> buildAdminPayoutConfigs(List<MerchantPayoutChannelConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            log.info("管理平台代付配置为空");
            return null;
        }

        Map<String, List<MerchantPayoutChannelConfig>> groupMap = configs.stream()
                .collect(Collectors.groupingBy(MerchantPayoutChannelConfig::getPaymentMethod));
        log.info("管理平台代付配置分组完成, groupMap={}", groupMap.size());

        List<MerchantChannelConfigDTO> result = groupMap.entrySet().stream()
                .map(e -> buildAdminPayoutConfigDTO(e.getKey(), e.getValue()))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MerchantChannelConfigDTO::getPaymentMethod))
                .collect(Collectors.toList());
        log.info("管理平台代付配置构建完成, result={}", result.size());
        return result;
    }

    /**
     * 构建管理平台收款配置DTO
     */
    private MerchantChannelConfigDTO buildAdminPaymentConfigDTO(String paymentMethod, List<MerchantPaymentChannelConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            log.info("管理平台收款配置DTO构建失败, paymentMethod={}, configs为空", paymentMethod);
            return null;
        }

        MerchantPaymentChannelConfig config = configs.get(0);
        MerchantChannelConfigDTO dto = new MerchantChannelConfigDTO();
        dto.setPaymentMethod(paymentMethod);
        dto.setSingleRate(config.getSingleRate());
        dto.setSingleFee(config.getSingleFee());
        dto.setAmountLimitMin(config.getAmountLimitMin());
        dto.setAmountLimitMax(config.getAmountLimitMax());
        dto.setSettleType(config.getSettleType());
        dto.setSettleTime(config.getSettleTime());
        dto.setStatus(configs.stream().anyMatch(MerchantPaymentChannelConfig::isStatus));

        List<MerchantChannelConfigChannelDTO> channelList = configs.stream()
                .map(this::buildChannelDTO)
                .collect(Collectors.toSet())
                .stream()
                .sorted(Comparator.comparing(MerchantChannelConfigChannelDTO::getPriority))
                .collect(Collectors.toList());
        dto.setChannelList(channelList);
        log.info("管理平台收款配置DTO构建完成, paymentMethod={}, channelList={}", paymentMethod, channelList.size());
        return dto;
    }

    /**
     * 构建管理平台代付配置DTO
     */
    private MerchantChannelConfigDTO buildAdminPayoutConfigDTO(String paymentMethod, List<MerchantPayoutChannelConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            log.info("管理平台代付配置DTO构建失败, paymentMethod={}, configs为空", paymentMethod);
            return null;
        }

        MerchantPayoutChannelConfig config = configs.get(0);
        MerchantChannelConfigDTO dto = new MerchantChannelConfigDTO();
        dto.setPaymentMethod(paymentMethod);
        dto.setSingleRate(config.getSingleRate());
        dto.setSingleFee(config.getSingleFee());
        dto.setAmountLimitMin(config.getAmountLimitMin());
        dto.setAmountLimitMax(config.getAmountLimitMax());
        dto.setSettleType(config.getSettleType());
        dto.setSettleTime(config.getSettleTime());
        dto.setStatus(configs.stream().anyMatch(MerchantPayoutChannelConfig::isStatus));

        List<MerchantChannelConfigChannelDTO> channelList = configs.stream()
                .map(this::buildChannelDTO)
                .collect(Collectors.toSet())
                .stream()
                .sorted(Comparator.comparing(MerchantChannelConfigChannelDTO::getPriority))
                .collect(Collectors.toList());
        dto.setChannelList(channelList);
        log.info("管理平台代付配置DTO构建完成, paymentMethod={}, channelList={}", paymentMethod, channelList.size());
        return dto;
    }

    /**
     * 构建渠道DTO
     */
    private MerchantChannelConfigChannelDTO buildChannelDTO(MerchantPaymentChannelConfig config) {
        MerchantChannelConfigChannelDTO dto = new MerchantChannelConfigChannelDTO();
        dto.setId(config.getId());
        dto.setChannelCode(config.getChannelCode());
        dto.setChannelName(config.getChannelName());
        dto.setPriority(config.getPriority());
        dto.setStatus(config.isStatus());
        return dto;
    }

    /**
     * 构建渠道DTO
     */
    private MerchantChannelConfigChannelDTO buildChannelDTO(MerchantPayoutChannelConfig config) {
        MerchantChannelConfigChannelDTO dto = new MerchantChannelConfigChannelDTO();
        dto.setId(config.getId());
        dto.setChannelCode(config.getChannelCode());
        dto.setChannelName(config.getChannelName());
        dto.setPriority(config.getPriority());
        dto.setStatus(config.isStatus());
        return dto;
    }
}
