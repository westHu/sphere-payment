package com.paysphere.job;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.TradeFileJobCmdService;
import com.paysphere.command.cmd.TradeFileJobCommand;
import com.paysphere.convert.JobConverter;
import com.paysphere.job.param.TradeFileJobParam;
import com.paysphere.mq.RocketMqProducer;
import com.xxl.job.core.context.XxlJobHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

import static com.paysphere.TradeConstant.JOB_NAME_TRADE_FILE;

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
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 每日交易数据进行打包成文件继而进行对账
     */
    // @XxlJob(JOB_NAME_TRADE_FILE)
    public void handler() {
        log.info("Job name:[{}]. Start time={}", JOB_NAME_TRADE_FILE, LocalDateTime.now());

        TradeFileJobParam param = getTradeFileJobParam();
        log.info("Job name:[{}]. Param={}", JOB_NAME_TRADE_FILE, JSONUtil.toJsonStr(param));

        try {
            TradeFileJobCommand command = jobConverter.convertMerchantFileJobCommand(param);
            tradeFileJobCmdService.handlerTradeFile(command);
        } catch (Exception e) {
            log.info("Job name:[{}]. End time={}. Exception", JOB_NAME_TRADE_FILE, LocalDateTime.now(), e);
            rocketMqProducer.syncSendJobMessage("交易对账文件", "Exceptional", e.getMessage());
            return;
        }
        log.info("Job name:[{}]. End time={}", JOB_NAME_TRADE_FILE, LocalDateTime.now());
    }

    /**
     * 解析参数
     */
    private TradeFileJobParam getTradeFileJobParam() {
        String jobParam = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(jobParam)) {
            return new TradeFileJobParam();
        } else {
            return JSONUtil.toBean(jobParam, TradeFileJobParam.class);
        }
    }

}