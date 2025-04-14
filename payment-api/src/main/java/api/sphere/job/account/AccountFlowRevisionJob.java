package api.sphere.job.account;

import api.sphere.job.param.AccountFlowRevisionJobParam;
import cn.hutool.json.JSONUtil;
import app.sphere.command.SettleAccountJobCmdService;
import app.sphere.command.cmd.SettleAccountFlowRevisionJobCommand;
import api.sphere.convert.JobConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static share.sphere.TradeConstant.JOB_NAME_ACCOUNT_FLOW_REVISION;


/**
 * 余额校对
 */
@Slf4j
@Component
public class AccountFlowRevisionJob {

    @Resource
    JobConverter jobConverter;
    @Resource
    SettleAccountJobCmdService settleAccountJobCmdService;

    /**
     * 取某个切点时间, 查询某个账户的瞬时余额和当前的流水最大值，进行流水和余额的校对
     */
    public void accountFlowRevisionHandler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_ACCOUNT_FLOW_REVISION, LocalDateTime.now());

        AccountFlowRevisionJobParam param = new AccountFlowRevisionJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_ACCOUNT_FLOW_REVISION, JSONUtil.toJsonStr(param));

        try {
            SettleAccountFlowRevisionJobCommand command = jobConverter.convertAccountFlowRevisionJobCommand(param);
            settleAccountJobCmdService.accountFlowRevision(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_ACCOUNT_FLOW_REVISION, LocalDateTime.now(), e);
            return;
        }

        log.info("Job name:[{}]. End time={}", JOB_NAME_ACCOUNT_FLOW_REVISION, LocalDateTime.now());
    }

}