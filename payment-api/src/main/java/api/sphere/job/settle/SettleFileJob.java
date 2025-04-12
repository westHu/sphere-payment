package api.sphere.job.settle;

import api.sphere.job.param.SettleFileJobParam;
import cn.hutool.json.JSONUtil;
import app.sphere.command.SettleFileJobCmdService;
import app.sphere.command.cmd.SettleFileJobCommand;
import api.sphere.convert.SettleConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static share.sphere.TradeConstant.JOB_NAME_SETTLE_FILE;


/**
 * 结算文件
 */
@Slf4j
@Component
public class SettleFileJob {

    @Resource
    SettleConverter settleConverter;
    @Resource
    SettleFileJobCmdService settleFileJobCmdService;

    /**
     * 每日结算数据进行打包成文件
     */
    public void handler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_SETTLE_FILE, LocalDateTime.now());

        SettleFileJobParam param = new SettleFileJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_SETTLE_FILE, JSONUtil.toJsonStr(param));

        try {
            SettleFileJobCommand command = settleConverter.convertSettleFileJobCommand(param);
            settleFileJobCmdService.handlerSettleFile(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_SETTLE_FILE, LocalDateTime.now(), e);
            return;
        }

        log.info("Job name:[{}]. End time={}", JOB_NAME_SETTLE_FILE, LocalDateTime.now());
    }


}