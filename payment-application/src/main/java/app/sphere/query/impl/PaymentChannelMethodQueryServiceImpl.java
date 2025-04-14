package app.sphere.query.impl;

import app.sphere.query.PaymentChannelMethodQueryService;
import app.sphere.query.dto.ChannelPaymentMethodGroupDTO;
import app.sphere.query.dto.PaymentChannelMethodFeeRangeDTO;
import app.sphere.query.dto.PaymentChannelMethodGroupDTO;
import app.sphere.query.param.PaymentChannelMethodGroupParam;
import app.sphere.query.param.PaymentChannelMethodPageParam;
import app.sphere.query.param.PaymentChannelMethodParam;
import app.sphere.query.param.PaymentChannelMethodRangeParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.PaymentChannelMethodRepository;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import share.sphere.enums.PaymentDirectionEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static share.sphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class PaymentChannelMethodQueryServiceImpl implements PaymentChannelMethodQueryService {

    private static final String GROUP_SPLIT = "-";
    @Resource
    PaymentChannelMethodRepository paymentChannelMethodRepository;

    @Override
    public List<PaymentChannelMethodGroupDTO> groupPaymentChannelMethodList(PaymentChannelMethodGroupParam param) {
        QueryWrapper<PaymentChannelMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(StringUtils.isNotBlank(param.getChannelCode()), PaymentChannelMethod::getChannelCode, param.getChannelCode())
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), PaymentChannelMethod::getPaymentMethod, param.getPaymentMethod())
                .eq(Objects.nonNull(param.getPaymentDirection()), PaymentChannelMethod::getPaymentDirection, param.getPaymentDirection());
        List<PaymentChannelMethod> channelMethodList = paymentChannelMethodRepository.list(queryWrapper);
        if (CollectionUtils.isEmpty(channelMethodList)) {
            return new ArrayList<>();
        }

        //分组
        Map<String, List<PaymentChannelMethod>> groupMap = channelMethodList.stream()
                .collect(Collectors.groupingBy(e -> e.getPaymentMethod() + GROUP_SPLIT + e.getPaymentDirection()));
        if (MapUtils.isEmpty(groupMap)) {
            return new ArrayList<>();
        }

        //合并
        return groupMap.entrySet().stream().map(m -> {
            String[] split = m.getKey().split(GROUP_SPLIT);
            String paymentMethod = split[0];
            String paymentDirection = split[1];
            Integer direction = Optional.ofNullable(paymentDirection).map(Integer::parseInt)
                    .orElse(PaymentDirectionEnum.TRANSACTION.getCode());
            List<PaymentChannelMethod> groupChannelMethodList = m.getValue();

            PaymentChannelMethodGroupDTO groupDTO = new PaymentChannelMethodGroupDTO();
            groupDTO.setPaymentMethod(paymentMethod.toUpperCase());//转大写
            groupDTO.setPaymentDirection(direction);
            groupDTO.setStatus(groupChannelMethodList.stream().anyMatch(PaymentChannelMethod::isStatus));
            groupDTO.setCount(groupChannelMethodList.size());
            groupDTO.setPaymentChannelMethodList(groupChannelMethodList);
            return groupDTO;
        }).sorted(Comparator.comparing(PaymentChannelMethodGroupDTO::getPaymentMethod)).toList();
    }

    @Override
    public List<ChannelPaymentMethodGroupDTO> groupChannelPaymentMethodList(PaymentChannelMethodGroupParam param) {
        QueryWrapper<PaymentChannelMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(StringUtils.isNotBlank(param.getChannelCode()), PaymentChannelMethod::getChannelCode, param.getChannelCode())
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), PaymentChannelMethod::getPaymentMethod, param.getPaymentMethod())
                .eq(Objects.nonNull(param.getPaymentDirection()), PaymentChannelMethod::getPaymentDirection, param.getPaymentDirection());
        List<PaymentChannelMethod> channelMethodList = paymentChannelMethodRepository.list(queryWrapper);
        if (CollectionUtils.isEmpty(channelMethodList)) {
            return new ArrayList<>();
        }

        //分组
        Map<String, List<PaymentChannelMethod>> groupMap = channelMethodList.stream()
                .collect(Collectors.groupingBy(e -> e.getChannelCode() + GROUP_SPLIT + e.getChannelName()));
        if (MapUtils.isEmpty(groupMap)) {
            return new ArrayList<>();
        }

        //合并
        return groupMap.entrySet().stream().map(m -> {
            String[] split = m.getKey().split(GROUP_SPLIT);
            String channelCode = split[0];
            String channelName = split[1];

            List<PaymentChannelMethod> groupChannelMethodList = m.getValue();
            groupChannelMethodList.forEach(e -> e.setPaymentMethod(e.getPaymentMethod().toUpperCase()));

            ChannelPaymentMethodGroupDTO groupDTO = new ChannelPaymentMethodGroupDTO();
            groupDTO.setChannelCode(channelCode);
            groupDTO.setChannelName(channelName);
            groupDTO.setStatus(groupChannelMethodList.stream().anyMatch(PaymentChannelMethod::isStatus));
            groupDTO.setCount(groupChannelMethodList.size());
            groupDTO.setPaymentChannelMethodList(groupChannelMethodList);
            return groupDTO;
        }).sorted(Comparator.comparing(ChannelPaymentMethodGroupDTO::getChannelCode)).collect(Collectors.toList());
    }

    @Override
    public Page<PaymentChannelMethod> pagePaymentChannelMethodList(PaymentChannelMethodPageParam param) {
        QueryWrapper<PaymentChannelMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(StringUtils.isNotBlank(param.getChannelCode()), PaymentChannelMethod::getChannelCode
                        , param.getChannelCode())
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), PaymentChannelMethod::getPaymentMethod,
                        param.getPaymentMethod())
                .eq(Objects.nonNull(param.getPaymentDirection()), PaymentChannelMethod::getPaymentDirection,
                        param.getPaymentDirection())
                .eq(Objects.nonNull(param.getStatus()), PaymentChannelMethod::isStatus, param.getStatus());

        return paymentChannelMethodRepository.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
    }

    @Override
    public PaymentChannelMethod getPaymentChannelMethod(PaymentChannelMethodParam param) {
        QueryWrapper<PaymentChannelMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getChannelCode()), PaymentChannelMethod::getChannelCode, param.getChannelCode())
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), PaymentChannelMethod::getPaymentMethod, param.getPaymentMethod())
                .eq(Objects.nonNull(param.getPaymentDirection()), PaymentChannelMethod::getPaymentDirection, param.getPaymentDirection())
                .eq(PaymentChannelMethod::isStatus, true)
                .last(LIMIT_1);
        return paymentChannelMethodRepository.getOne(queryWrapper);
    }

    @Override
    public PaymentChannelMethodFeeRangeDTO getPaymentChannelMethodFeeRange(PaymentChannelMethodRangeParam param) {
        QueryWrapper<PaymentChannelMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PaymentChannelMethod::getPaymentMethod, param.getPaymentMethod())
                .eq(PaymentChannelMethod::getPaymentDirection, param.getPaymentDirection());
        List<PaymentChannelMethod> channelMethodList = paymentChannelMethodRepository.list(queryWrapper);

        BigDecimal singleFee = BigDecimal.ZERO; //取其最高
        BigDecimal singleRate = BigDecimal.ZERO; //取其最高
        BigDecimal amountLimitMin = BigDecimal.ZERO; //取其最大
        BigDecimal amountLimitMax = new BigDecimal("1000000000"); //取其最小

        for (PaymentChannelMethod channelMethod : channelMethodList) {
            //取其最小
            if (channelMethod.getSingleFee().compareTo(singleFee) > 0) {
                singleFee = channelMethod.getSingleFee();
            }
            //取其最小
            if (channelMethod.getSingleRate().compareTo(singleRate) > 0) {
                singleRate = channelMethod.getSingleRate();
            }
            //取其最大
            if (channelMethod.getAmountLimitMin().compareTo(amountLimitMin) > 0) {
                amountLimitMin = channelMethod.getAmountLimitMin();
            }
            //取其最小
            if (channelMethod.getAmountLimitMax().compareTo(amountLimitMax) > 0) {
                amountLimitMax = channelMethod.getAmountLimitMax();
            }
        }

        PaymentChannelMethodFeeRangeDTO dto = new PaymentChannelMethodFeeRangeDTO();
        dto.setPaymentMethod(param.getPaymentMethod());
        dto.setSingleFee(singleFee);
        dto.setSingleRate(singleRate);
        dto.setAmountLimitMin(amountLimitMin);
        dto.setAmountLimitMax(amountLimitMax);
        return dto;
    }


}
