package app.sphere.query.impl;

import app.sphere.query.PaymentMethodQueryService;
import app.sphere.query.param.PaymentMethodPageParam;
import app.sphere.query.param.PaymentMethodParam;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.PaymentChannelMethodRepository;
import domain.sphere.repository.PaymentMethodRepository;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
import infrastructure.sphere.db.entity.PaymentMethod;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import share.sphere.enums.PaymentDirectionEnum;

import java.util.*;
import java.util.stream.Collectors;

import static share.sphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class PaymentMethodQueryServiceImpl implements PaymentMethodQueryService {

    @Resource
    PaymentMethodRepository paymentMethodRepository;
    @Resource
    PaymentChannelMethodRepository paymentChannelMethodRepository;


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
        return paymentMethodRepository.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
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
        return paymentMethodRepository.getOne(methodQuery);
    }

    @Override
    public List<PaymentMethod> getPaymentMethodList4Transaction() {
        QueryWrapper<PaymentChannelMethod> channelMethodQuery = new QueryWrapper<>();
        channelMethodQuery.lambda()
//                .eq(PaymentChannelMethod::getPaymentDirection, PaymentDirectionEnum.TRANSACTION.getCode())
                .eq(PaymentChannelMethod::isStatus, true);
        List<PaymentChannelMethod> channelMethodList = paymentChannelMethodRepository.list(channelMethodQuery);

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
        return paymentMethodRepository.list(methodQuery);
    }

    @Override
    public List<PaymentMethod> getPaymentMethodList4Disbursement() {
        QueryWrapper<PaymentChannelMethod> channelMethodQuery = new QueryWrapper<>();
        channelMethodQuery.lambda()
                .eq(PaymentChannelMethod::getPaymentDirection, PaymentDirectionEnum.DISBURSEMENT.getCode())
                .eq(PaymentChannelMethod::isStatus, true);
        List<PaymentChannelMethod> channelMethodList = paymentChannelMethodRepository.list(channelMethodQuery);

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
        return paymentMethodRepository.list(methodQuery);
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
        return paymentChannelMethodRepository.getOne(methodQuery);
    }
}
