package api.sphere.job.settle;

import api.sphere.job.param.SettleJobParam;
import cn.hutool.json.JSONUtil;
import app.sphere.command.SettleJobCmdService;
import app.sphere.command.cmd.SettleJobCommand;
import api.sphere.convert.JobConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static share.sphere.TradeConstant.JOB_NAME_D2_PAY_SETTLE;


/**
 * 定时结算
 */
@Slf4j
@Component
public class D2SettleOrderPayJob  {


    @Resource
    SettleJobCmdService settleJobCmdService;
    @Resource
    JobConverter jobConverter;



    /**
     * 每隔x分钟定时执行结算任务
     */
    public void handler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_D2_PAY_SETTLE, LocalDateTime.now());



        SettleJobParam param = new SettleJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_D2_PAY_SETTLE, JSONUtil.toJsonStr(param));

        try {
            SettleJobCommand command = jobConverter.convertSettleJobCommand(param);
            settleJobCmdService.fixD2TimeSettlePay(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_D2_PAY_SETTLE, LocalDateTime.now(), e);
            return;
        }

        log.info("Job name:[{}]. End time={}", JOB_NAME_D2_PAY_SETTLE, LocalDateTime.now());
    }

}