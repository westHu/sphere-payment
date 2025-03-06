package com.paysphere.job.settle;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.SettleJobCmdService;
import com.paysphere.command.cmd.SettleJobCommand;
import com.paysphere.convert.JobConverter;
import com.paysphere.job.param.SettleJobParam;
import com.paysphere.mq.RocketMqProducer;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.paysphere.TradeConstant.JOB_NAME_D2_PAY_SETTLE;


/**
 * 定时结算
 */
@Slf4j
@Component
public class D2SettleOrderPayJob extends AbstractSettlePayJob {


    @Resource
    SettleJobCmdService settleJobCmdService;
    @Resource
    JobConverter jobConverter;
    @Resource
    RocketMqProducer rocketMqProducer;


    /**
     * 每隔x分钟定时执行结算任务
     */
    @XxlJob(JOB_NAME_D2_PAY_SETTLE)
    public void handler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_D2_PAY_SETTLE, LocalDateTime.now());



        SettleJobParam param = getSettleJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_D2_PAY_SETTLE, JSONUtil.toJsonStr(param));

        try {
            SettleJobCommand command = jobConverter.convertSettleJobCommand(param);
            settleJobCmdService.fixD2TimeSettlePay(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_D2_PAY_SETTLE, LocalDateTime.now(), e);
            rocketMqProducer.syncSendExceptionMessage("D2结算, 任务异常:" + e.getMessage());
            return;
        }

        log.info("Job name:[{}]. End time={}", JOB_NAME_D2_PAY_SETTLE, LocalDateTime.now());
    }

}