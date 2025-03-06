package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.command.SettleJobCmdService;
import com.paysphere.command.cmd.SettleJobCommand;
import com.paysphere.db.entity.SettleOrder;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.SettleTypeEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.mq.dto.SettlePayMessageDTO;
import com.paysphere.repository.SettleOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.paysphere.TradeConstant.SETTLE_PAY_TOPIC;

@Slf4j
@Service
public class SettleJobCmdServiceImpl implements SettleJobCmdService {

    private static final String T00 = " 00:00:00";
    private static final int SIZE = 100000;

    @Resource
    SettleOrderService settleOrderService;
    @Resource
    RocketMqProducer rocketMqProducer;


    @Override
    public void fixD0TimeSettlePay(SettleJobCommand command) {
        log.info("fixD0TimeSettlePay command={}", JSONUtil.toJsonStr(command));

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime beginTime = endTime.minusDays(1);
        log.info("fixD0TimeSettlePay beginTime={} endTime={}", beginTime, endTime);


        //查询'待结算、结算失败'的订单
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .between(SettleOrder::getPaymentFinishTime, beginTime, endTime) //过去24小时
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode()) //只针对收款;
                .eq(SettleOrder::getSettleType, SettleTypeEnum.D0.name()) //D0
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle())
                .last("LIMIT " + SIZE);
        List<SettleOrder> settleOrderList = settleOrderService.list(queryWrapper);
        log.info("fixD0TimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettlePayMessageDTO messageDTO = new SettlePayMessageDTO();
            messageDTO.setTradeNo(order.getTradeNo());
            messageDTO.setSettleType(SettleTypeEnum.JOB.name()); //设置为JOB
            SendResult sendResult = rocketMqProducer.syncSend(SETTLE_PAY_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("fixD0TimeSettlePay message={}", sendResult);
        });

    }

    /**
     * 2次任务 是否可以扫到重复数据 ？
     */
    @Override
    public void fixD1TimeSettlePay(SettleJobCommand command) {
        log.info("fixD1TimeSettlePay command={}", JSONUtil.toJsonStr(command));

        //当前时间
        LocalTime nowTime = LocalTime.now();
        //能结算的日期（再此日期之前）
        String beginTime = LocalDate.now().minusDays(1) + T00;
        String endTime = LocalDate.now() + T00;
        log.info("fixD1TimeSettlePay nowTime={} beginTime={} endTime={}", nowTime, beginTime, endTime);

        //查询'待结算、结算失败'的订单
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .between(SettleOrder::getPaymentFinishTime, beginTime, endTime) //支付完成时间小于今天凌晨，至少是D-1
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode()) //只针对收款;
                .eq(SettleOrder::getSettleType, SettleTypeEnum.D1.name()) //D1
                .lt(SettleOrder::getSettleTime, nowTime.format(TradeConstant.DF_1)) //结算时间小于当前时间
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle())
                .last("LIMIT " + SIZE);
        List<SettleOrder> settleOrderList = settleOrderService.list(queryWrapper);
        log.info("fixD1TimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettlePayMessageDTO messageDTO = new SettlePayMessageDTO();
            messageDTO.setTradeNo(order.getTradeNo());
            messageDTO.setSettleType(SettleTypeEnum.JOB.name()); //设置为JOB
            SendResult sendResult = rocketMqProducer.syncSend(SETTLE_PAY_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("fixD1TimeSettlePay message={}", sendResult);
        });
    }


    @Override
    public void fixD2TimeSettlePay(SettleJobCommand command) {
        log.info("fixD2TimeSettlePay command={}", JSONUtil.toJsonStr(command));

        //当前时间
        LocalTime nowTime = LocalTime.now();

        //能结算的日期（再此日期之前）
        String beginTime = LocalDate.now().minusDays(2) + T00;
        String endTime = LocalDate.now().minusDays(1) + T00;
        log.info("fixD2TimeSettlePay nowTime={}, beginTime={} endTime={}", nowTime, beginTime, endTime);

        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .between(SettleOrder::getPaymentFinishTime, beginTime, endTime) //支付完成时间小于d-1凌晨，至少是D-2
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode()) //只针对收款;
                .eq(SettleOrder::getSettleType, SettleTypeEnum.D2.name()) //D2
                .lt(SettleOrder::getSettleTime, nowTime.format(TradeConstant.DF_1)) //结算时间小于当前时间
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle())
                .last("LIMIT " + SIZE);
        List<SettleOrder> settleOrderList = settleOrderService.list(queryWrapper);
        log.info("fixD2TimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettlePayMessageDTO messageDTO = new SettlePayMessageDTO();
            messageDTO.setTradeNo(order.getTradeNo());
            messageDTO.setSettleType(SettleTypeEnum.JOB.name()); //设置为JOB
            SendResult sendResult = rocketMqProducer.syncSend(SETTLE_PAY_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("fixD2TimeSettlePay message={}", sendResult);
        });

    }




    @Override
    public void fixT1TimeSettlePay(SettleJobCommand command) {
        log.info("fixT1TimeSettlePay command={}", JSONUtil.toJsonStr(command));

        //今日凌晨
        LocalTime nowTime = LocalTime.now();
        LocalDate nowDate = LocalDate.now();
       /* if (WorkDayUtil.isWeekend(nowDate)) {
            log.warn("fixT1TimeSettlePay. Today is weekend nowTime={}, nowDate={}", nowTime, nowDate);
            return;
        }*/

        log.info("fixT1TimeSettlePay nowTime={}, nowDate={}", nowTime, nowDate);
        String beginTime = nowDate.minusDays(3) + T00;
        String endTime = nowDate + T00;

        //查询'待结算、结算失败'的订单
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .between(SettleOrder::getPaymentFinishTime, beginTime, endTime) //支付完成时间小于今日凌晨，至少是T-1
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode()) //只针对收款;
                .eq(SettleOrder::getSettleType, SettleTypeEnum.T1.name()) //T1
                .lt(SettleOrder::getSettleTime, nowTime.format(TradeConstant.DF_1)) //结算时间小于当前时间
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle())
                .last("LIMIT " + SIZE);
        List<SettleOrder> settleOrderList = settleOrderService.list(queryWrapper);
        log.info("fixT1TimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettlePayMessageDTO messageDTO = new SettlePayMessageDTO();
            messageDTO.setTradeNo(order.getTradeNo());
            messageDTO.setSettleType(SettleTypeEnum.JOB.name()); //设置为JOB
            SendResult sendResult = rocketMqProducer.syncSend(SETTLE_PAY_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("fixT1TimeSettlePay message={}", sendResult);
        });
    }


    @Override
    public void fixT2TimeSettlePay(SettleJobCommand command) {
        log.info("fixT2TimeSettlePay command={}", JSONUtil.toJsonStr(command));

        //今日凌晨
        LocalTime nowTime = LocalTime.now();

        //能结算的日期（再此日期之前）
        String endTime = null; //WorkDayUtil.lastOneWorkingDay(LocalDate.now()) + T00;
        log.info("fixT2TimeSettlePay nowTime={}, endTime={}", nowTime, endTime);

        //查询'待结算、结算失败'的订单
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(SettleOrder::getPaymentFinishTime, endTime) //支付完成时间小于T-1凌晨，至少是T-2
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode()) //只针对收款;
                .eq(SettleOrder::getSettleType, SettleTypeEnum.T2.name()) //T2
                .lt(SettleOrder::getSettleTime, nowTime.format(TradeConstant.DF_1)) //结算时间小于当前时间
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle())
                .last("LIMIT " + SIZE);
        List<SettleOrder> settleOrderList = settleOrderService.list(queryWrapper);
        log.info("fixT2TimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettlePayMessageDTO messageDTO = new SettlePayMessageDTO();
            messageDTO.setTradeNo(order.getTradeNo());
            messageDTO.setSettleType(SettleTypeEnum.JOB.name()); //设置为JOB
            SendResult sendResult = rocketMqProducer.syncSend(SETTLE_PAY_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("fixT2TimeSettlePay message={}", sendResult);
        });
    }


    @Override
    public void fixT0TimeSettlePay(SettleJobCommand command) {
        log.info("fixT0TimeSettlePay command={}", JSONUtil.toJsonStr(command));

        //判断下, 该方法周末不执行
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        log.info("fixT0TimeSettlePay dayOfWeek={}", dayOfWeek);
        if (DayOfWeek.SATURDAY.equals(dayOfWeek) || DayOfWeek.SUNDAY.equals(dayOfWeek)) {
            log.warn("fixT0TimeSettlePay weekend {} M0 need not settle", dayOfWeek.name());
            return;
        }

        //上一个周末
        LocalDate nowDate = LocalDate.now();
        LocalDate lastSaturday = null; //WorkDayUtil.lastSaturday(nowDate);
        LocalDate lastSunday = lastSaturday.plusDays(1);
        log.info("fixT0TimeSettlePay lastSaturday={}, lastSunday={}", lastSaturday, lastSunday);
        String start = lastSaturday + " 00:00:00";
        String end = lastSunday + " 23:59:59";

        //查询'待结算、结算失败'的订单
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .between(SettleOrder::getPaymentFinishTime, start, end) //支付完成时间, 上一个整个周末
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode()) //只针对收款;
                .eq(SettleOrder::getSettleType, SettleTypeEnum.T0.name()) //T0
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle())
                .last("LIMIT " + SIZE);
        List<SettleOrder> settleOrderList = settleOrderService.list(queryWrapper);
        log.info("fixT0TimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettlePayMessageDTO messageDTO = new SettlePayMessageDTO();
            messageDTO.setTradeNo(order.getTradeNo());
            messageDTO.setSettleType(SettleTypeEnum.JOB.name()); //设置为JOB
            SendResult sendResult = rocketMqProducer.syncSend(SETTLE_PAY_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("fixT0TimeSettlePay message={}", sendResult);
        });
    }

    @Override
    public void fixManualTimeSettlePay(SettleJobCommand command) {
        log.info("fixManualTimeSettlePay command={}", JSONUtil.toJsonStr(command));

        String merchantId = command.getMerchantId();
        String startTradeTime = command.getStartTradeTime();
        String endTradeTime = command.getEndTradeTime();

        //查询'待结算、结算失败'的订单
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .between(SettleOrder::getPaymentFinishTime, startTradeTime, endTradeTime)
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode()) //只针对收款;
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle())
                .eq(SettleOrder::getMerchantId, merchantId)
                .last("LIMIT " + SIZE);
        List<SettleOrder> settleOrderList = settleOrderService.list(queryWrapper);
        log.info("fixManualTimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettlePayMessageDTO messageDTO = new SettlePayMessageDTO();
            messageDTO.setTradeNo(order.getTradeNo());
            messageDTO.setSettleType(SettleTypeEnum.JOB.name()); //设置为JOB
            SendResult sendResult = rocketMqProducer.syncSend(SETTLE_PAY_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("fixManualTimeSettlePay message={}", sendResult);
        });

    }

}


