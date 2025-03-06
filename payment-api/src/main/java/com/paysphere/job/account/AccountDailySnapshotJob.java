package com.paysphere.job.account;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.SettleAccountJobCmdService;
import com.paysphere.command.cmd.SettleAccountSnapshotJobCommand;
import com.paysphere.convert.JobConverter;
import com.paysphere.job.param.AccountSnapshotJobParam;
import com.paysphere.mq.RocketMqProducer;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.paysphere.TradeConstant.JOB_NAME_ACCOUNT_DAILY_SNAPSHOT;


/**
 * 余额 SNAPSHOT
 */
@Slf4j
@Component
public class AccountDailySnapshotJob {

    @Resource
    JobConverter jobConverter;
    @Resource
    SettleAccountJobCmdService settleAccountJobCmdService;
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 每日00:00触发生成余额快照, 时间需要设置在00:00分，金额是可能存在略微误差的
     */
    @XxlJob(JOB_NAME_ACCOUNT_DAILY_SNAPSHOT)
    public void accountSnapshotHandler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_ACCOUNT_DAILY_SNAPSHOT, LocalDateTime.now());

        AccountSnapshotJobParam param = getAccountSnapshotJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_ACCOUNT_DAILY_SNAPSHOT, JSONUtil.toJsonStr(param));

        try {
            SettleAccountSnapshotJobCommand command = jobConverter.convertAccountSnapshotJobCommand(param);
            settleAccountJobCmdService.accountDailySnapshot(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_ACCOUNT_DAILY_SNAPSHOT, LocalDateTime.now(), e);
            rocketMqProducer.syncSendExceptionMessage("每日余额快照, 任务异常:" + e.getMessage());
            return;
        }

        log.info("Job name:[{}]. End time={}", JOB_NAME_ACCOUNT_DAILY_SNAPSHOT, LocalDateTime.now());
    }

    /**
     * 构建参数
     */
    private AccountSnapshotJobParam getAccountSnapshotJobParam() {
        if (StringUtils.isBlank(XxlJobHelper.getJobParam())) {
            return new AccountSnapshotJobParam();
        } else {
            return JSONUtil.toBean(XxlJobHelper.getJobParam(), AccountSnapshotJobParam.class);
        }
    }

}