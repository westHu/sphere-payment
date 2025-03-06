package com.paysphere.job.account;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.SettleAccountJobCmdService;
import com.paysphere.command.cmd.SettleAccountFlowRevisionJobCommand;
import com.paysphere.convert.JobConverter;
import com.paysphere.job.param.AccountFlowRevisionJobParam;
import com.paysphere.mq.RocketMqProducer;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.paysphere.TradeConstant.JOB_NAME_ACCOUNT_FLOW_REVISION;


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
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 取某个切点时间, 查询某个账户的瞬时余额和当前的流水最大值，进行流水和余额的校对
     */
    @XxlJob(JOB_NAME_ACCOUNT_FLOW_REVISION)
    public void accountFlowRevisionHandler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_ACCOUNT_FLOW_REVISION, LocalDateTime.now());

        AccountFlowRevisionJobParam param = getAccountFlowRevisionJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_ACCOUNT_FLOW_REVISION, JSONUtil.toJsonStr(param));

        try {
            SettleAccountFlowRevisionJobCommand command = jobConverter.convertAccountFlowRevisionJobCommand(param);
            settleAccountJobCmdService.accountFlowRevision(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_ACCOUNT_FLOW_REVISION, LocalDateTime.now(), e);
            rocketMqProducer.syncSendExceptionMessage("余额&流水校对, 任务异常:" + e.getMessage());
            return;
        }

        log.info("Job name:[{}]. End time={}", JOB_NAME_ACCOUNT_FLOW_REVISION, LocalDateTime.now());
    }

    /**
     * 构建参数
     */
    private AccountFlowRevisionJobParam getAccountFlowRevisionJobParam() {
        if (StringUtils.isBlank(XxlJobHelper.getJobParam())) {
            return new AccountFlowRevisionJobParam();
        } else {
            return JSONUtil.toBean(XxlJobHelper.getJobParam(), AccountFlowRevisionJobParam.class);
        }
    }

}