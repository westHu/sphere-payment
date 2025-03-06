package com.paysphere.query.param;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;


@Data
public class OperatorParam {

    /**
     * 操作人
     */
    private String operator;

    /**
     * 请求来源
     */
    private String source;

    public String getSource() {
        return StringUtils.isBlank(source) ? "Trade" : source;
    }
}
