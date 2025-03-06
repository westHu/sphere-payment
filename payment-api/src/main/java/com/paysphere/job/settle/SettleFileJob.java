package com.paysphere.job.settle;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.SettleFileJobCmdService;
import com.paysphere.command.cmd.SettleFileJobCommand;
import com.paysphere.convert.SettleConverter;
import com.paysphere.job.param.SettleFileJobParam;
import com.paysphere.mq.RocketMqProducer;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.paysphere.TradeConstant.JOB_NAME_SETTLE_FILE;


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
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 每日结算数据进行打包成文件
     */
    @XxlJob(JOB_NAME_SETTLE_FILE)
    public void handler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_SETTLE_FILE, LocalDateTime.now());

        SettleFileJobParam param = getSettleFileJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_SETTLE_FILE, JSONUtil.toJsonStr(param));

        try {
            SettleFileJobCommand command = settleConverter.convertSettleFileJobCommand(param);
            settleFileJobCmdService.handlerSettleFile(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_SETTLE_FILE, LocalDateTime.now(), e);
            rocketMqProducer.syncSendExceptionMessage("每日结算对账文件, 任务异常:" + e.getMessage());
            return;
        }

        log.info("Job name:[{}]. End time={}", JOB_NAME_SETTLE_FILE, LocalDateTime.now());
    }

    /**
     * 解析参数
     */
    private SettleFileJobParam getSettleFileJobParam() {
        if (StringUtils.isBlank(XxlJobHelper.getJobParam())) {
            return new SettleFileJobParam();
        } else {
            return JSONUtil.toBean(XxlJobHelper.getJobParam(), SettleFileJobParam.class);
        }
    }
}