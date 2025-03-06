package com.paysphere.command.impl;

import com.paysphere.command.SettleCashCmdService;
import com.paysphere.command.SettleOrderCmdService;
import com.paysphere.command.SettlePayCmdService;
import com.paysphere.repository.SettleOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SettleOrderCmdServiceImpl implements SettleOrderCmdService {

    @Resource
    SettleOrderService settleOrderService;
    @Resource
    SettlePayCmdService settlePayCmdService;
    @Resource
    SettleCashCmdService settleCashCmdService;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;


    //--------

}
