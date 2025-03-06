package com.paysphere.sensitive;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import lombok.Data;

/**
 * Sensitive
 */
@Data
public class SensitiveLogbackPatternLayoutEncoder extends PatternLayoutEncoder {

    /**
     * 正则替换规则
     */
    private SensitiveLogbackReplaces replaces;
    /**
     * 是否开启脱敏，默认关闭(false）
     */
    private Boolean sensitive = false;

    /**
     * 使用自定义 SensitiveLogbackPatternLayout 格式化输出
     */
    @Override
    public void start() {
        SensitiveLogbackPatternLayout patternLayout = new SensitiveLogbackPatternLayout(replaces, sensitive);
        patternLayout.setContext(context);
        patternLayout.setPattern(this.getPattern());
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        started = true;
    }
}