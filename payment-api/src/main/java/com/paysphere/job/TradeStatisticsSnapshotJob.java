package com.paysphere.job;


import cn.hutool.json.JSONUtil;
import com.paysphere.command.TradeStatisticsSnapshotJobCmdService;
import com.paysphere.command.cmd.TradeStatisticsSnapshotJobCommand;
import com.paysphere.convert.JobConverter;
import com.paysphere.job.param.TradeStatisticsSnapshotJobParam;
import com.paysphere.mq.RocketMqProducer;
import com.xxl.job.core.context.XxlJobHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

import static com.paysphere.TradeConstant.JOB_NAME_TRADE_STATISTICS;

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
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 隔日计算首页需要展示的数据
     */
    // @XxlJob(JOB_NAME_TRADE_STATISTICS)
    public void handler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_TRADE_STATISTICS, LocalDateTime.now());

        TradeStatisticsSnapshotJobParam param = getTradeStatisticsSnapshotJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_TRADE_STATISTICS, JSONUtil.toJsonStr(param));

        try {
            TradeStatisticsSnapshotJobCommand command = jobConverter.convertTradeStatisticsSnapshotJobCommand(param);
            tradeStatisticsSnapshotJobCmdService.handlerTradeStatisticsSnapshot(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_TRADE_STATISTICS, LocalDateTime.now(), e);
            rocketMqProducer.syncSendJobMessage("交易数据分析", "Exceptional", e.getMessage());
            return;
        }
        log.info("Job name:[{}]. End time={}", JOB_NAME_TRADE_STATISTICS, LocalDateTime.now());
    }

    /**
     * 构建参数
     */
    private TradeStatisticsSnapshotJobParam getTradeStatisticsSnapshotJobParam() {
        if (StringUtils.isBlank(XxlJobHelper.getJobParam())) {
            return new TradeStatisticsSnapshotJobParam();
        } else {
            return JSONUtil.toBean(XxlJobHelper.getJobParam(), TradeStatisticsSnapshotJobParam.class);
        }
    }
}
