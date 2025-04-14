package app.sphere.query.impl;

import app.sphere.query.SettleOrderQueryService;
import app.sphere.query.dto.SettleGroupDTO;
import app.sphere.query.param.*;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.SettleOrderRepository;
import infrastructure.sphere.db.entity.SettleOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import share.sphere.enums.SettleStatusEnum;

import java.util.*;
import java.util.stream.Collectors;

import static share.sphere.TradeConstant.LIMIT_1;


@Slf4j
@Service
public class SettleOrderQueryServiceImpl implements SettleOrderQueryService {

    @Resource
    SettleOrderRepository settleOrderRepository;


    @Override
    public List<SettleGroupDTO> groupSettleList(SettleGroupListParam param) {
        log.info("groupSettleList param = {}", JSONUtil.toJsonStr(param));

        QueryWrapper<SettleOrder> settleOrderQuery = new QueryWrapper<>();
        settleOrderQuery.select("merchant_id as merchantId, " +
                "merchant_name as merchantName, " +
                "payment_method as paymentMethod, " +
                "channel_code as channelCode, " +
                "channel_name as channelName, " +
                "trade_type as tradeType, " +
                "sum(merchant_fee) as merchantFee, " +
                "sum(merchant_profit) as merchantProfit, " +
                "sum(channel_cost) as channelCost, " +
                "sum(platform_profit) as platformProfit, " +
                "sum(account_amount) as accountAmount");
        settleOrderQuery.lambda().eq(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_SUCCESS.getCode())
                //.in(SettleOrder::getTradeType, TradeTypeEnum.groupList()) //收款代付
                .isNotNull(SettleOrder::getPaymentMethod)
                .isNotNull(SettleOrder::getChannelCode)
                .between(SettleOrder::getTradeTime, param.getTradeStartTime(), param.getTradeEndTime())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), SettleOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), SettleOrder::getPaymentMethod,
                        param.getPaymentMethod())
                .eq(StringUtils.isNotBlank(param.getChannelCode()), SettleOrder::getChannelCode, param.getChannelCode())
                .groupBy(SettleOrder::getMerchantId)
                .groupBy(SettleOrder::getPaymentMethod)
                .groupBy(SettleOrder::getChannelCode)
                .groupBy(SettleOrder::getTradeType);
        List<SettleOrder> orderList = settleOrderRepository.list(settleOrderQuery);
        if (CollectionUtils.isEmpty(orderList)) {
            return new ArrayList<>();
        }

        return orderList.stream().map(e -> {
            SettleGroupDTO dto = new SettleGroupDTO();
            dto.setMerchantId(e.getMerchantId());
            dto.setMerchantName(e.getMerchantName());
            dto.setPaymentMethod(e.getPaymentMethod());
            dto.setChannelCode(e.getChannelCode());
            dto.setChannelName(e.getChannelName());
            dto.setTradeType(e.getTradeType());
            dto.setCurrency(e.getCurrency());
            dto.setMerchantFee(e.getMerchantFee());
            dto.setMerchantProfit(e.getMerchantProfit());
            dto.setChannelCost(e.getChannelCost());
            dto.setPlatformProfit(e.getPlatformProfit());
            dto.setAccountAmount(e.getAccountAmount());
            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public Page<SettleOrder> pageSettleOrderList(SettleOrderPageParam param) {
        log.info("pageSettleOrderList param = {}", JSONUtil.toJsonStr(param));

        QueryWrapper<SettleOrder> settleOrderQuery = new QueryWrapper<>();
        settleOrderQuery.lambda()
                .eq(StringUtils.isNotBlank(param.getTradeNo()), SettleOrder::getTradeNo, param.getTradeNo())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), SettleOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), SettleOrder::getPaymentMethod, param.getPaymentMethod())
                .eq(StringUtils.isNotBlank(param.getChannelCode()), SettleOrder::getChannelCode, param.getChannelCode())
                .eq(Objects.nonNull(param.getSettleStatus()), SettleOrder::getSettleStatus, param.getSettleStatus())
                .ge(StringUtils.isNotBlank(param.getTradeStartTime()), SettleOrder::getTradeTime, param.getTradeStartTime())
                .le(StringUtils.isNotBlank(param.getTradeEndTime()), SettleOrder::getTradeTime, param.getTradeEndTime());
        return settleOrderRepository.page(new Page<>(param.getPageNum(), param.getPageSize()), settleOrderQuery);
    }

    @Override
    public SettleOrder getSettleOrder(SettleOrderParam param) {
        log.info("getSettleOrder param = {}", JSONUtil.toJsonStr(param));

        QueryWrapper<SettleOrder> settleOrderQuery = new QueryWrapper<>();
        settleOrderQuery.lambda().eq(StringUtils.isNotBlank(param.getTradeNo()), SettleOrder::getTradeNo, param.getTradeNo())
                .last(LIMIT_1);
        return settleOrderRepository.getOne(settleOrderQuery);
    }

}
