package api.sphere.job;

import api.sphere.convert.JobConverter;
import api.sphere.job.param.TradeFileJobParam;
import app.sphere.command.TradeFileJobCmdService;
import app.sphere.command.cmd.TradeFileJobCommand;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static share.sphere.TradeConstant.JOB_NAME_TRADE_FILE;

/**
 * 交易记录文件
 */
@Slf4j
// @Component
public class TradeFileJob {

    @Resource
    JobConverter jobConverter;
    @Resource
    TradeFileJobCmdService tradeFileJobCmdService;

    /**
     * 每日交易数据进行打包成文件继而进行对账
     */
    public void handler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_TRADE_FILE, LocalDateTime.now());

        TradeFileJobParam param = new TradeFileJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_TRADE_FILE, JSONUtil.toJsonStr(param));

        try {
            TradeFileJobCommand command = jobConverter.convertMerchantFileJobCommand(param);
            tradeFileJobCmdService.handlerTradeFile(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_TRADE_FILE, LocalDateTime.now(), e);
            return;
        }
        log.info("Job name:[{}]. End time={}", JOB_NAME_TRADE_FILE, LocalDateTime.now());
    }


}