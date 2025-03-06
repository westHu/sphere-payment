package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.command.TradeJobCmdService;
import com.paysphere.command.cmd.TradePayOrderTimeOutJobCommand;
import com.paysphere.db.entity.BaseEntity;
import com.paysphere.db.entity.TradePaymentLinkOrder;
import com.paysphere.db.entity.TradePaymentOrder;
import com.paysphere.enums.TradePaymentLinkStatusEnum;
import com.paysphere.enums.TradePaymentSourceEnum;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.repository.TradePaymentLinkOrderService;
import com.paysphere.repository.TradePaymentOrderService;
import com.paysphere.repository.TradePayoutOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TradeJobCmdServiceImpl implements TradeJobCmdService {

    @Resource
    TradePaymentOrderService tradePaymentOrderService;
    @Resource
    TradePayoutOrderService tradePayoutOrderService;
    @Resource
    TradePaymentLinkOrderService tradePaymentLinkOrderService;
    @Resource
    RocketMqProducer rocketMqProducer;

    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;



    /**
     * 时间可放置到凌晨执行
     * FIXME根据索引加上时间条件
     */
    @Override
    public void handlerTimeOut(TradePayOrderTimeOutJobCommand command) {
        log.info("handlerTimeOut command={}", JSONUtil.toJsonStr(command));

        // 计算时间为 当前时间 - 有效期限
        LocalDateTime expiryPeriod = LocalDateTime.now().plusSeconds(-TradeConstant.TRADE_EXPIRY_PERIOD_MAX);
        log.info("handlerTimeOut expiryPeriod={}", expiryPeriod);

        // 查询需要设置超时的订单 针对创建了初始化订单，未选择支付方式的订单
        QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradePaymentOrder::getTradeStatus, TradeStatusEnum.TRADE_INIT.getCode()) // 状态初始化
                .le(TradePaymentOrder::getTradeTime, expiryPeriod); // 时间小于n秒前的
        List<TradePaymentOrder> initPayOrderList = tradePaymentOrderService.list(payOrderQuery);
        log.info("handlerTimeOut initPayOrderList size={}", initPayOrderList.size());

        if (CollectionUtils.isNotEmpty(initPayOrderList)) {
            List<Long> initIdList = initPayOrderList.stream().map(BaseEntity::getId).toList();
            UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
            payOrderUpdate.lambda()
                    .set(TradePaymentOrder::getTradeStatus, TradeStatusEnum.TRADE_EXPIRED.getCode())
                    //.set(TradePayOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_EXPIRED.getCode())
                    .in(TradePaymentOrder::getId, initIdList);
            tradePaymentOrderService.update(payOrderUpdate);

            // 如果是PayLink
            List<String> initPayLinkNoList = initPayOrderList.stream()
                    .filter(e -> e.getSource().equals(TradePaymentSourceEnum.PAY_LINK.getCode()))
                    .map(TradePaymentOrder::getOrderNo)
                    .toList();
            if (CollectionUtils.isNotEmpty(initPayLinkNoList)) {
                UpdateWrapper<TradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
                linkOrderUpdate.lambda()
                        .set(TradePaymentLinkOrder::getLinkStatus, TradePaymentLinkStatusEnum.PAYMENT_LINK_EXPIRED.getCode())
                        .in(TradePaymentLinkOrder::getLinkNo, initPayLinkNoList);
                tradePaymentLinkOrderService.update(linkOrderUpdate);
            }
        }

        // 查询需要设置超时的订单 针对下单成功，拿到Va等，但未支付
       /* payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradePayOrder::getTradeStatus, TradeStatusEnum.TRADE_SUCCESS.getCode()) // 状态成功
                .eq(TradePayOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_UNPAID.getCode())
                .le(TradePayOrder::getTradeTime, expiryPeriod); // 时间小于n秒前的
        List<TradePayOrder> unpaidPayOrderList = tradePayOrderService.list(payOrderQuery);
        if (CollectionUtils.isNotEmpty(unpaidPayOrderList)) {

            List<Long> unpaidIdList = unpaidPayOrderList.stream().map(BaseEntity::getId).toList();
            UpdateWrapper<TradePayOrder> payOrderUpdate = new UpdateWrapper<>();
            payOrderUpdate.lambda().set(TradePayOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_OVER_TIME.getCode())
                    .in(TradePayOrder::getId, unpaidIdList);
            tradePayOrderService.update(payOrderUpdate);

            // 如果是PayLink
            List<String> unpaidPayLinkNoList = unpaidPayOrderList.stream().filter(e -> e.getSource()
                            .equals(TradePaySourceEnum.PAY_LINK.getCode()))
                    .map(TradePayOrder::getOuterNo)
                    .toList();
            if (CollectionUtils.isNotEmpty(unpaidPayLinkNoList)) {
                UpdateWrapper<TradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
                linkOrderUpdate.lambda().set(TradePaymentLinkOrder::getLinkStatus, TradeStatusEnum.TRADE_OVER_TIME
                .getCode())
                        .in(TradePaymentLinkOrder::getLinkNo, unpaidPayLinkNoList);
                tradePaymentLinkOrderService.update(linkOrderUpdate);
            }
        }*/

    }


}
