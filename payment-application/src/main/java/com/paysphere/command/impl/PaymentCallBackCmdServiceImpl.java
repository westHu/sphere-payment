package com.paysphere.command.impl;

import com.paysphere.command.PaymentCallBackCmdService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class PaymentCallBackCmdServiceImpl implements PaymentCallBackCmdService {

    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;


}
