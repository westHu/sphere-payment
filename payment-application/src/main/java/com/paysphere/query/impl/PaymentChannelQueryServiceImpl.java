package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.paysphere.db.entity.PaymentChannel;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.query.PaymentChannelQueryService;
import com.paysphere.query.param.PaymentChannelListParam;
import com.paysphere.query.param.PaymentChannelPageParam;
import com.paysphere.repository.PaymentChannelMethodService;
import com.paysphere.repository.PaymentChannelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentChannelQueryServiceImpl implements PaymentChannelQueryService {

    @Resource
    PaymentChannelService paymentChannelService;
    @Resource
    PaymentChannelMethodService paymentChannelMethodService;

    @Override
    public Page<PaymentChannel> pagePaymentChannelList(PaymentChannelPageParam param) {
        log.info("pagePaymentChannelList param={}", JSONUtil.toJsonStr(param));

        List<String> relateChannelCodeList = null;
        if (Objects.nonNull(param.getRelated()) && param.getRelated()) { //FIXME
            QueryWrapper<PaymentChannelMethod> channelMethodQuery = new QueryWrapper<>();
            channelMethodQuery.select("DISTINCT channel_code as channelCode");
            channelMethodQuery.lambda()
                    .eq(StringUtils.isNotBlank(param.getChannelCode()), PaymentChannelMethod::getChannelCode,
                            param.getChannelCode())
                    .eq(StringUtils.isNotBlank(param.getChannelName()), PaymentChannelMethod::getChannelName,
                            param.getChannelName());
            List<PaymentChannelMethod> channelMethodList = paymentChannelMethodService.list(channelMethodQuery);
            if (CollectionUtils.isEmpty(channelMethodList)) {
                return new Page<>();
            }
            relateChannelCodeList = channelMethodList.stream()
                    .map(PaymentChannelMethod::getChannelCode)
                    .collect(Collectors.toList());
        }

        QueryWrapper<PaymentChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getChannelCode()), PaymentChannel::getChannelCode, param.getChannelCode())
                .eq(StringUtils.isNotBlank(param.getChannelName()), PaymentChannel::getChannelName, param.getChannelName())
                .in(CollectionUtils.isNotEmpty(relateChannelCodeList), PaymentChannel::getChannelCode, relateChannelCodeList)
                .eq(Objects.nonNull(param.getStatus()), PaymentChannel::isStatus, param.getStatus());

        return paymentChannelService.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
    }


    @Override
    public List<PaymentChannel> getPaymentChannelList(PaymentChannelListParam param) {
        QueryWrapper<PaymentChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getChannelCode()), PaymentChannel::getChannelCode, param.getChannelCode())
                .eq(StringUtils.isNotBlank(param.getChannelName()), PaymentChannel::getChannelName, param.getChannelName())

                .eq(Objects.nonNull(param.getStatus()), PaymentChannel::isStatus, param.getStatus());

        return paymentChannelService.list(queryWrapper);
    }

}
