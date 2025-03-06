package com.paysphere.job;


import cn.hutool.json.JSONUtil;
import com.paysphere.command.TradeJobCmdService;
import com.paysphere.command.cmd.TradePayOrderTimeOutJobCommand;
import com.paysphere.convert.JobConverter;
import com.paysphere.job.param.TradePayOrderTimeOutJobParam;
import com.paysphere.mq.RocketMqProducer;
import com.xxl.job.core.context.XxlJobHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

import static com.paysphere.TradeConstant.JOB_NAME_TRADE_PAY_TIMEOUT;

/**
 * 预付订单超时任务
 */
@Slf4j
// @Component
public class TradePayOrderTimeOutJob {


    @Resource
    JobConverter jobConverter;
    @Resource
    TradeJobCmdService tradeJobCmdService;
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 每隔N小时进行对满足状态的订单进行更新为 “超时”
     */
    // @XxlJob(JOB_NAME_TRADE_PAY_TIMEOUT)
    public void handler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_TRADE_PAY_TIMEOUT, LocalDateTime.now());

        TradePayOrderTimeOutJobParam param = getTradePayOrderTimeOutJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_TRADE_PAY_TIMEOUT, JSONUtil.toJsonStr(param));
        try {
            TradePayOrderTimeOutJobCommand command = jobConverter.convertTradePayOrderTimeOutJobCommand(param);
            tradeJobCmdService.handlerTimeOut(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_TRADE_PAY_TIMEOUT, LocalDateTime.now(), e);
            rocketMqProducer.syncSendJobMessage("订单过期校验", "Exceptional", e.getMessage());
            return;
        }
        log.info("Job name:[{}]. End time={}", JOB_NAME_TRADE_PAY_TIMEOUT, LocalDateTime.now());
    }

    /**
     * 解析参数
     */
    private TradePayOrderTimeOutJobParam getTradePayOrderTimeOutJobParam() {
        String jobParam = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(jobParam)) {
            return new TradePayOrderTimeOutJobParam();
        } else {
            return JSONUtil.toBean(jobParam, TradePayOrderTimeOutJobParam.class);
        }
    }

}
