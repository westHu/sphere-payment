package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.db.entity.PaymentMethod;
import com.paysphere.enums.PaymentDirectionEnum;
import com.paysphere.query.PaymentMethodQueryService;
import com.paysphere.query.param.PaymentMethodPageParam;
import com.paysphere.query.param.PaymentMethodParam;
import com.paysphere.repository.PaymentChannelMethodService;
import com.paysphere.repository.PaymentMethodService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class PaymentMethodQueryServiceImpl implements PaymentMethodQueryService {

    @Resource
    PaymentMethodService paymentMethodService;
    @Resource
    PaymentChannelMethodService paymentChannelMethodService;


    @Override
    public Page<PaymentMethod> pagePaymentMethodList(PaymentMethodPageParam param) {
        log.info("pagePaymentMethodList param={}", JSONUtil.toJsonStr(param));

        QueryWrapper<PaymentMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Objects.nonNull(param.getPaymentType()), PaymentMethod::getPaymentType,
                        param.getPaymentType())
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), PaymentMethod::getPaymentMethod,
                        param.getPaymentMethod())
                .eq(Objects.nonNull(param.getStatus()), PaymentMethod::isStatus, param.getStatus());
        if (Objects.nonNull(param.getPaymentDirection())) {
            String directionSql = "and payment_direction&" + param.getPaymentDirection() + " > 0";
            queryWrapper.last(directionSql);
        }
        return paymentMethodService.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
    }


    @Override
    public PaymentMethod getPaymentMethod(PaymentMethodParam param) {
        log.info("getPaymentMethod param={}", JSONUtil.toJsonStr(param));

        QueryWrapper<PaymentMethod> methodQuery = new QueryWrapper<>();
        methodQuery.lambda()
                .gt(PaymentMethod::getPaymentDirection, param.getPaymentDirection())
                .in(PaymentMethod::getPaymentMethod, param.getPaymentMethod())
                .eq(Objects.nonNull(param.getStatus()), PaymentMethod::isStatus, param.getStatus())
                .last(LIMIT_1);
        return paymentMethodService.getOne(methodQuery);
    }


    @Override
    public List<PaymentMethod> getPaymentMethodList4Transaction() {
        QueryWrapper<PaymentChannelMethod> channelMethodQuery = new QueryWrapper<>();
        channelMethodQuery.lambda()
//                .eq(PaymentChannelMethod::getPaymentDirection, PaymentDirectionEnum.TRANSACTION.getCode())
                .eq(PaymentChannelMethod::isStatus, true);
        List<PaymentChannelMethod> channelMethodList = paymentChannelMethodService.list(channelMethodQuery);

        List<String> paymentMethodList = channelMethodList.stream()
                .map(PaymentChannelMethod::getPaymentMethod)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(channelMethodList)) {
            return new ArrayList<>();
        }

        QueryWrapper<PaymentMethod> methodQuery = new QueryWrapper<>();
        methodQuery.lambda()
                .in(PaymentMethod::getPaymentMethod, paymentMethodList)
                .eq(PaymentMethod::isStatus, true);
        methodQuery.last("and payment_direction&" + PaymentDirectionEnum.TRANSACTION.getCode() + " > 0");
        return paymentMethodService.list(methodQuery);
    }

    @Override
    public List<PaymentMethod> getPaymentMethodList4Disbursement() {
        QueryWrapper<PaymentChannelMethod> channelMethodQuery = new QueryWrapper<>();
        channelMethodQuery.lambda()
                .eq(PaymentChannelMethod::getPaymentDirection, PaymentDirectionEnum.DISBURSEMENT.getCode())
                .eq(PaymentChannelMethod::isStatus, true);
        List<PaymentChannelMethod> channelMethodList = paymentChannelMethodService.list(channelMethodQuery);

        List<String> paymentMethodList = channelMethodList.stream()
                .map(PaymentChannelMethod::getPaymentMethod)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(channelMethodList)) {
            return new ArrayList<>();
        }

        QueryWrapper<PaymentMethod> methodQuery = new QueryWrapper<>();
        methodQuery.lambda()
                .in(PaymentMethod::getPaymentMethod, paymentMethodList)
                .eq(PaymentMethod::isStatus, true);
        methodQuery.last("and payment_direction&" + PaymentDirectionEnum.DISBURSEMENT.getCode() + " > 0");
        return paymentMethodService.list(methodQuery);
    }


    @Override
    public PaymentChannelMethod getDisbursementPaymentChannelMethod(String channelCode, String paymentMethod) {
        log.info("getPaymentMethod param={} {}", channelCode, paymentMethod);

        QueryWrapper<PaymentChannelMethod> methodQuery = new QueryWrapper<>();
        methodQuery.lambda()
                .eq(PaymentChannelMethod::getPaymentDirection, PaymentDirectionEnum.DISBURSEMENT.getCode())
                .eq(PaymentChannelMethod::getPaymentMethod, paymentMethod)
                .eq(PaymentChannelMethod::getChannelCode, channelCode)
                .eq(PaymentChannelMethod::isStatus, true)
                .last(LIMIT_1);
        return paymentChannelMethodService.getOne(methodQuery);
    }
}
