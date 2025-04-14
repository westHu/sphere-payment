package api.sphere.job.account;

import api.sphere.convert.JobConverter;
import api.sphere.job.param.AccountSnapshotJobParam;
import app.sphere.command.SettleAccountJobCmdService;
import app.sphere.command.cmd.SettleAccountSnapshotJobCommand;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static share.sphere.TradeConstant.JOB_NAME_ACCOUNT_DAILY_SNAPSHOT;


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

    /**
     * 每日00:00触发生成余额快照, 时间需要设置在00:00分，金额是可能存在略微误差的
     */
    public void accountSnapshotHandler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_ACCOUNT_DAILY_SNAPSHOT, LocalDateTime.now());

        AccountSnapshotJobParam param = new AccountSnapshotJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_ACCOUNT_DAILY_SNAPSHOT, JSONUtil.toJsonStr(param));

        try {
            SettleAccountSnapshotJobCommand command = jobConverter.convertAccountSnapshotJobCommand(param);
            settleAccountJobCmdService.accountDailySnapshot(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_ACCOUNT_DAILY_SNAPSHOT, LocalDateTime.now(), e);
            return;
        }

        log.info("Job name:[{}]. End time={}", JOB_NAME_ACCOUNT_DAILY_SNAPSHOT, LocalDateTime.now());
    }

}