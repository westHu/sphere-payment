package api.sphere.job;


import api.sphere.convert.JobConverter;
import api.sphere.job.param.TradeStatisticsSnapshotJobParam;
import app.sphere.command.TradeStatisticsSnapshotJobCmdService;
import app.sphere.command.cmd.TradeStatisticsSnapshotJobCommand;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static share.sphere.TradeConstant.JOB_NAME_TRADE_STATISTICS;

/**
 * 交易分析数据快照
 * 按日计算首页展示数据
 */
@Slf4j
// @Component
public class TradeStatisticsSnapshotJob {

    @Resource
    JobConverter jobConverter;
    @Resource
    TradeStatisticsSnapshotJobCmdService tradeStatisticsSnapshotJobCmdService;

    /**
     * 隔日计算首页需要展示的数据
     */
    public void handler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_TRADE_STATISTICS, LocalDateTime.now());

        TradeStatisticsSnapshotJobParam param = new TradeStatisticsSnapshotJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_TRADE_STATISTICS, JSONUtil.toJsonStr(param));

        try {
            TradeStatisticsSnapshotJobCommand command = jobConverter.convertTradeStatisticsSnapshotJobCommand(param);
            tradeStatisticsSnapshotJobCmdService.handlerTradeStatisticsSnapshot(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_TRADE_STATISTICS, LocalDateTime.now(), e);
            return;
        }
        log.info("Job name:[{}]. End time={}", JOB_NAME_TRADE_STATISTICS, LocalDateTime.now());
    }

}
