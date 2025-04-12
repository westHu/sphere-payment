package api.sphere.job;


import cn.hutool.json.JSONUtil;
import app.sphere.command.TradeJobCmdService;
import app.sphere.command.cmd.TradePayOrderTimeOutJobCommand;
import api.sphere.convert.JobConverter;
import api.sphere.job.param.TradePayOrderTimeOutJobParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static share.sphere.TradeConstant.JOB_NAME_TRADE_PAY_TIMEOUT;

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

    /**
     * 每隔N小时进行对满足状态的订单进行更新为 “超时”
     */
    // @XxlJob(JOB_NAME_TRADE_PAY_TIMEOUT)
    public void handler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_TRADE_PAY_TIMEOUT, LocalDateTime.now());

        TradePayOrderTimeOutJobParam param = new TradePayOrderTimeOutJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_TRADE_PAY_TIMEOUT, JSONUtil.toJsonStr(param));
        try {
            TradePayOrderTimeOutJobCommand command = jobConverter.convertTradePayOrderTimeOutJobCommand(param);
            tradeJobCmdService.handlerTimeOut(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_TRADE_PAY_TIMEOUT, LocalDateTime.now(), e);
            return;
        }
        log.info("Job name:[{}]. End time={}", JOB_NAME_TRADE_PAY_TIMEOUT, LocalDateTime.now());
    }


}
