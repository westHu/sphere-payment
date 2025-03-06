package com.paysphere.job.settle;

import cn.hutool.json.JSONUtil;
import com.paysphere.job.param.SettleJobParam;
import com.xxl.job.core.context.XxlJobHelper;
import org.apache.commons.lang3.StringUtils;

public class AbstractSettlePayJob {

    protected SettleJobParam getSettleJobParam() {
        SettleJobParam param;
        if (StringUtils.isBlank(XxlJobHelper.getJobParam())) {
            param = new SettleJobParam();
        } else {
            param = JSONUtil.toBean(XxlJobHelper.getJobParam(), SettleJobParam.class);
        }
        return param;
    }
}
