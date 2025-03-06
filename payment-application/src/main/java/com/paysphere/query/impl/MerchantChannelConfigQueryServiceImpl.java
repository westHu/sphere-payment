package com.paysphere.query.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.db.entity.MerchantPaymentChannelConfig;
import com.paysphere.db.entity.MerchantPayoutChannelConfig;
import com.paysphere.enums.MerchantQuerySourceEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.query.MerchantChannelConfigQueryService;
import com.paysphere.query.dto.MerchantChannelConfigChannelDTO;
import com.paysphere.query.dto.MerchantChannelConfigDTO;
import com.paysphere.query.dto.MerchantChannelConfigListDTO;
import com.paysphere.query.param.MerchantChannelConfigListParam;
import com.paysphere.repository.MerchantConfigService;
import com.paysphere.repository.MerchantPaymentChannelConfigService;
import com.paysphere.repository.MerchantPayoutChannelConfigService;
import com.paysphere.repository.MerchantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantChannelConfigQueryServiceImpl implements MerchantChannelConfigQueryService {

    @Resource
    MerchantService merchantService;
    @Resource
    MerchantPaymentChannelConfigService merchantPaymentChannelConfigService;
    @Resource
    MerchantPayoutChannelConfigService merchantPayoutChannelConfigService;
    @Resource
    MerchantConfigService merchantConfigService;


    @Override
    public MerchantChannelConfigListDTO getMerchantChannelConfigList(MerchantChannelConfigListParam param) {
        log.info("getMerchantChannelConfigList param={}", JSONUtil.toJsonStr(param));

        String merchantId = param.getMerchantId();
        MerchantChannelConfigListDTO channelConfigListDTO = new MerchantChannelConfigListDTO();

        //查询商户配置
        QueryWrapper<MerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantConfig::getMerchantId, merchantId).last(LIMIT_1);
        MerchantConfig merchantConfig = merchantConfigService.getOne(configQuery);
        Assert.notNull(merchantConfig, () -> new PaymentException(ExceptionCode.MERCHANT_CONFIG_NOT_EXIST, merchantId));

        MerchantQuerySourceEnum sourceEnum = MerchantQuerySourceEnum.codeToEnum(param.getQuerySource());
        log.info("getMerchantChannelConfigList sourceEnum={}", sourceEnum.toString());

        //查询收款配置
        CompletableFuture<Void> f0 = CompletableFuture.runAsync(() -> {
            //查询支付方式
            QueryWrapper<MerchantPaymentChannelConfig> payPaymentConfigQuery = new QueryWrapper<>();
            payPaymentConfigQuery.lambda().eq(MerchantPaymentChannelConfig::getMerchantId, merchantId);
            List<MerchantPaymentChannelConfig> configList = merchantPaymentChannelConfigService.list(payPaymentConfigQuery);
            if (CollectionUtils.isEmpty(configList)) {
                return;
            }

            //商户平台
            if (sourceEnum.equals(MerchantQuerySourceEnum.MERCHANT_ADMIN)) {
                //分组
                Map<String, List<MerchantPaymentChannelConfig>> groupMap = configList.stream()
                        .collect(Collectors.groupingBy(MerchantPaymentChannelConfig::getPaymentMethod));
                log.info("getMerchantChannelConfigList pay groupMap={}", groupMap.size());

                //查询支付方式
                List<MerchantChannelConfigDTO> paymentDTOList = groupMap.entrySet().stream().map(e -> {
                            String key = e.getKey(); //paymentMethod
                            List<MerchantPaymentChannelConfig> value = e.getValue(); //支付方式下支持的渠道
                            if (CollectionUtils.isEmpty(value)) {
                                return null;
                            }

                            //是否有渠道开启支持该支付方式
                            boolean anyMatch = value.stream().anyMatch(MerchantPaymentChannelConfig::isStatus);
                            if (!anyMatch) {
                                return null;
                            }

                            MerchantPaymentChannelConfig config = value.get(0);
                            MerchantChannelConfigDTO paymentDTO = new MerchantChannelConfigDTO();
                            paymentDTO.setPaymentMethod(key);
                            paymentDTO.setPaymentIcon(null);
                            paymentDTO.setSingleRate(config.getSingleRate());
                            paymentDTO.setSingleFee(config.getSingleFee());
                            paymentDTO.setAmountLimitMin(config.getAmountLimitMin());
                            paymentDTO.setAmountLimitMax(config.getAmountLimitMax());
                            paymentDTO.setSettleType(config.getSettleType());
                            paymentDTO.setStatus(true);
                            return paymentDTO;
                        }).filter(Objects::nonNull)
                        .sorted(Comparator.comparing(MerchantChannelConfigDTO::getPaymentMethod))
                        .collect(Collectors.toList());
                channelConfigListDTO.setPaymentChannelConfigList(paymentDTOList);
            }

            //管理平台 查询渠道
            if (sourceEnum.equals(MerchantQuerySourceEnum.ADMIN)) {
                //分组
                Map<String, List<MerchantPaymentChannelConfig>> groupMap = configList.stream()
                        .collect(Collectors.groupingBy(MerchantPaymentChannelConfig::getPaymentMethod));
                log.info("getMerchantChannelConfigList pay groupMap={}", groupMap.size());

                List<MerchantChannelConfigDTO> paymentDTOList = groupMap.entrySet().stream().map(e -> {
                            String key = e.getKey(); //paymentMethod
                            List<MerchantPaymentChannelConfig> value = e.getValue(); //支付方式下支持的渠道
                            if (CollectionUtils.isEmpty(value)) {
                                return null;
                            }

                            MerchantPaymentChannelConfig config = value.get(0);
                            MerchantChannelConfigDTO paymentDTO = new MerchantChannelConfigDTO();
                            paymentDTO.setPaymentMethod(key);
                            paymentDTO.setSingleRate(config.getSingleRate());
                            paymentDTO.setSingleFee(config.getSingleFee());
                            paymentDTO.setAmountLimitMin(config.getAmountLimitMin());
                            paymentDTO.setAmountLimitMax(config.getAmountLimitMax());
                            paymentDTO.setSettleType(config.getSettleType());
                            paymentDTO.setSettleTime(config.getSettleTime());
                            boolean status = value.stream().anyMatch(MerchantPaymentChannelConfig::isStatus);
                            paymentDTO.setStatus(status);

                            List<MerchantChannelConfigChannelDTO> collect = value.stream().map(f -> {
                                MerchantChannelConfigChannelDTO channelDTO = new MerchantChannelConfigChannelDTO();
                                channelDTO.setId(f.getId());
                                channelDTO.setChannelCode(f.getChannelCode());
                                channelDTO.setChannelName(f.getChannelName());
                                channelDTO.setPriority(f.getPriority());
                                channelDTO.setStatus(f.isStatus());
                                return channelDTO;
                            }).sorted(Comparator.comparing(MerchantChannelConfigChannelDTO::getPriority)).collect(Collectors.toList());
                            paymentDTO.setChannelList(collect);
                            return paymentDTO;
                        }).filter(Objects::nonNull)
                        .filter(e -> StringUtils.isNotBlank(e.getPaymentMethod()))
                        .sorted(Comparator.comparing(MerchantChannelConfigDTO::getPaymentMethod))
                        .collect(Collectors.toList());
                channelConfigListDTO.setPayoutChannelConfigList(paymentDTOList);
            }
        });


        //查询代付配置
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            //查询支付方式
            QueryWrapper<MerchantPayoutChannelConfig> cashPaymentConfigQuery = new QueryWrapper<>();
            cashPaymentConfigQuery.lambda().eq(MerchantPayoutChannelConfig::getMerchantId, param.getMerchantId());
            List<MerchantPayoutChannelConfig> list = merchantPayoutChannelConfigService.list(cashPaymentConfigQuery);
            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            //商户平台
            if (sourceEnum.equals(MerchantQuerySourceEnum.MERCHANT_ADMIN)) {
                //分组
                Map<String, List<MerchantPayoutChannelConfig>> groupMap = list.stream()
                        .collect(Collectors.groupingBy(MerchantPayoutChannelConfig::getPaymentMethod));
                log.info("getMerchantChannelConfigList cash groupMap={}", groupMap.size());

                List<MerchantChannelConfigDTO> paymentDTOList = groupMap.entrySet().stream().map(e -> {
                            String key = e.getKey(); //paymentMethod
                            List<MerchantPayoutChannelConfig> value = e.getValue();
                            if (CollectionUtils.isEmpty(value)) {
                                return null;
                            }

                            boolean anyMatch = value.stream().anyMatch(MerchantPayoutChannelConfig::isStatus);
                            if (!anyMatch) {
                                return null;
                            }

                            //支付方式
                            log.info("getMerchantChannelConfigList cash value size={}", value.size());
                            MerchantPayoutChannelConfig config = value.get(0);
                            MerchantChannelConfigDTO paymentDTO = new MerchantChannelConfigDTO();
                            paymentDTO.setPaymentMethod(key);
                            paymentDTO.setSingleRate(config.getSingleRate());
                            paymentDTO.setSingleFee(config.getSingleFee());
                            paymentDTO.setAmountLimitMin(config.getAmountLimitMin());
                            paymentDTO.setAmountLimitMax(config.getAmountLimitMax());
                            paymentDTO.setSettleType(config.getSettleType());
                            paymentDTO.setStatus(true);
                            return paymentDTO;
                        }).filter(Objects::nonNull)
                        .sorted(Comparator.comparing(MerchantChannelConfigDTO::getPaymentMethod))
                        .collect(Collectors.toList());
                channelConfigListDTO.setPaymentChannelConfigList(paymentDTOList);
            }

            //管理平台 查询渠道
            if (sourceEnum.equals(MerchantQuerySourceEnum.ADMIN)) {
                //分组
                Map<String, List<MerchantPayoutChannelConfig>> groupMap = list.stream()
                        .collect(Collectors.groupingBy(MerchantPayoutChannelConfig::getPaymentMethod));
                log.info("getMerchantChannelConfigList cash groupMap={}", groupMap.size());

                List<MerchantChannelConfigDTO> paymentDTOList = groupMap.entrySet().stream().map(e -> {
                            String key = e.getKey(); //paymentMethod
                            List<MerchantPayoutChannelConfig> value = e.getValue();
                            if (CollectionUtils.isEmpty(value)) {
                                return null;
                            }

                            MerchantPayoutChannelConfig config = value.get(0);
                            MerchantChannelConfigDTO paymentDTO = new MerchantChannelConfigDTO();
                            paymentDTO.setPaymentMethod(key);
                            paymentDTO.setSingleRate(config.getSingleRate());
                            paymentDTO.setSingleFee(config.getSingleFee());
                            paymentDTO.setAmountLimitMin(config.getAmountLimitMin());
                            paymentDTO.setAmountLimitMax(config.getAmountLimitMax());
                            paymentDTO.setSettleType(config.getSettleType());
                            paymentDTO.setSettleTime(config.getSettleTime());
                            boolean status = value.stream().anyMatch(MerchantPayoutChannelConfig::isStatus);
                            paymentDTO.setStatus(status);

                            List<MerchantChannelConfigChannelDTO> collect = value.stream().map(f -> {
                                        MerchantChannelConfigChannelDTO channelDTO =
                                                new MerchantChannelConfigChannelDTO();
                                        channelDTO.setId(f.getId());
                                        channelDTO.setChannelCode(f.getChannelCode());
                                        channelDTO.setChannelName(f.getChannelName());
                                        channelDTO.setPriority(f.getPriority());
                                        channelDTO.setStatus(f.isStatus());
                                        return channelDTO;
                                    }).collect(Collectors.toSet()).stream()
                                    .sorted(Comparator.comparing(MerchantChannelConfigChannelDTO::getPriority))
                                    .collect(Collectors.toList());
                            paymentDTO.setChannelList(collect);

                            return paymentDTO;
                        }).filter(Objects::nonNull)
                        .filter(e -> StringUtils.isNotBlank(e.getPaymentMethod()))
                        .sorted(Comparator.comparing(MerchantChannelConfigDTO::getPaymentMethod)).collect(Collectors.toList());
                channelConfigListDTO.setPayoutChannelConfigList(paymentDTOList);
            }
        });

        CompletableFuture.allOf(f0, f1).join();
        return channelConfigListDTO;
    }


    //-----------------------------------------------------------------------------------------------------------------

}
