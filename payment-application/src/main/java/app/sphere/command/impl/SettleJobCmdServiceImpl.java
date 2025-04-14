package app.sphere.command.impl;

import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.SettleJobCmdService;
import app.sphere.command.cmd.SettleAccountUpdateSettleCommand;
import app.sphere.command.cmd.SettleJobCommand;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import domain.sphere.repository.SettleOrderRepository;
import infrastructure.sphere.db.entity.SettleOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.SettleStatusEnum;
import share.sphere.enums.SettleTimeEnum;
import share.sphere.enums.SettleTypeEnum;
import share.sphere.enums.TradeTypeEnum;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class SettleJobCmdServiceImpl implements SettleJobCmdService {

    @Resource
    SettleOrderRepository settleOrderRepository;
    @Resource
    SettleAccountCmdService settleAccountCmdService;

    /**
     * 2次任务 是否可以扫到重复数据 ？
     */
    @Override
    public void fixD1TimeSettlePay(SettleJobCommand command) {
        log.info("fixD1TimeSettlePay command={}", JSONUtil.toJsonStr(command));

        //当前时间
        LocalTime nowTime = LocalTime.now();
        //能结算的日期（再此日期之前）
        String beginTime = LocalDate.now().minusDays(1) + SettleTimeEnum.TIME_00.getTime();
        String endTime = LocalDate.now() + SettleTimeEnum.TIME_00.getTime();
        log.info("fixD1TimeSettlePay nowTime={} beginTime={} endTime={}", nowTime, beginTime, endTime);

        //查询'待结算、结算失败'的订单
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .between(SettleOrder::getPaymentFinishTime, beginTime, endTime)
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode())
                .eq(SettleOrder::getSettleType, SettleTypeEnum.D1.name())
                .lt(SettleOrder::getSettleTime, nowTime.format(TradeConstant.DF_1))
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle());
        List<SettleOrder> settleOrderList = settleOrderRepository.list(queryWrapper);
        log.info("fixD1TimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettleAccountUpdateSettleCommand settleCommand = new SettleAccountUpdateSettleCommand();
            BeanUtils.copyProperties(order, settleCommand);
            settleAccountCmdService.handlerAccountSettlement(settleCommand);
        });
    }

    @Override
    public void fixD2TimeSettlePay(SettleJobCommand command) {
        log.info("fixD2TimeSettlePay command={}", JSONUtil.toJsonStr(command));

        //当前时间
        LocalTime nowTime = LocalTime.now();

        //能结算的日期（再此日期之前）
        String beginTime = LocalDate.now().minusDays(2) + SettleTimeEnum.TIME_00.getTime();;
        String endTime = LocalDate.now().minusDays(1) + SettleTimeEnum.TIME_00.getTime();;
        log.info("fixD2TimeSettlePay nowTime={}, beginTime={} endTime={}", nowTime, beginTime, endTime);

        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .between(SettleOrder::getPaymentFinishTime, beginTime, endTime)
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode())
                .eq(SettleOrder::getSettleType, SettleTypeEnum.D2.name())
                .lt(SettleOrder::getSettleTime, nowTime.format(TradeConstant.DF_1))
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle());
        List<SettleOrder> settleOrderList = settleOrderRepository.list(queryWrapper);
        log.info("fixD2TimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettleAccountUpdateSettleCommand settleCommand = new SettleAccountUpdateSettleCommand();
            BeanUtils.copyProperties(order, settleCommand);
            settleAccountCmdService.handlerAccountSettlement(settleCommand);
        });

    }

    @Override
    public void fixT1TimeSettlePay(SettleJobCommand command) {
        log.info("fixT1TimeSettlePay command={}", JSONUtil.toJsonStr(command));

        //今日凌晨
        LocalTime nowTime = LocalTime.now();
        LocalDate nowDate = LocalDate.now();

        log.info("fixT1TimeSettlePay nowTime={}, nowDate={}", nowTime, nowDate);
        String beginTime = nowDate.minusDays(3) + SettleTimeEnum.TIME_00.getTime();;
        String endTime = nowDate + SettleTimeEnum.TIME_00.getTime();;

        //查询'待结算、结算失败'的订单
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .between(SettleOrder::getPaymentFinishTime, beginTime, endTime)
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode())
                .eq(SettleOrder::getSettleType, SettleTypeEnum.T1.name())
                .lt(SettleOrder::getSettleTime, nowTime.format(TradeConstant.DF_1))
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle());
        List<SettleOrder> settleOrderList = settleOrderRepository.list(queryWrapper);
        log.info("fixT1TimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettleAccountUpdateSettleCommand settleCommand = new SettleAccountUpdateSettleCommand();
            BeanUtils.copyProperties(order, settleCommand);
            settleAccountCmdService.handlerAccountSettlement(settleCommand);
        });
    }


    @Override
    public void fixT2TimeSettlePay(SettleJobCommand command) {
        log.info("fixT2TimeSettlePay command={}", JSONUtil.toJsonStr(command));

        //今日凌晨
        LocalTime nowTime = LocalTime.now();

        //能结算的日期（再此日期之前）
        String endTime = null;
        log.info("fixT2TimeSettlePay nowTime={}, endTime={}", nowTime, endTime);

        //查询'待结算、结算失败'的订单
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(SettleOrder::getPaymentFinishTime, endTime)
                .eq(SettleOrder::getTradeType, TradeTypeEnum.PAYMENT.getCode())
                .eq(SettleOrder::getSettleType, SettleTypeEnum.T2.name())
                .lt(SettleOrder::getSettleTime, nowTime.format(TradeConstant.DF_1))
                .in(SettleOrder::getSettleStatus, SettleStatusEnum.needToSettle());
        List<SettleOrder> settleOrderList = settleOrderRepository.list(queryWrapper);
        log.info("fixT2TimeSettlePay settleOrderList size={}", settleOrderList.size());

        settleOrderList.forEach(order -> {
            SettleAccountUpdateSettleCommand settleCommand = new SettleAccountUpdateSettleCommand();
            BeanUtils.copyProperties(order, settleCommand);
            settleAccountCmdService.handlerAccountSettlement(settleCommand);
        });
    }


}


