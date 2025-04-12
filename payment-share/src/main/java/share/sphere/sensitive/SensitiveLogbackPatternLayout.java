package share.sphere.sensitive;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Sensitive
 */
@Slf4j
public class SensitiveLogbackPatternLayout extends PatternLayout {

    /**
     * 正则替换规则
     */
    private final SensitiveLogbackReplaces replaces;
    /**
     * 是否开启脱敏，默认关闭(false）
     */
    private final Boolean sensitive;


    public SensitiveLogbackPatternLayout(SensitiveLogbackReplaces replaces, Boolean sensitive) {
        super();
        this.replaces = replaces;
        this.sensitive = sensitive;
    }

    /**
     * 格式化日志信息
     */
    @Override
    public String doLayout(ILoggingEvent event) {
        String msg = super.doLayout(event);
        return this.buildSensitiveMsg(msg);
    }

    /**
     * 根据配置对日志进行脱敏
     */
    public String buildSensitiveMsg(String msg) {
        if (sensitive == null || !sensitive) {
            return msg;
        }
        if (this.replaces == null || this.replaces.getReplace() == null || this.replaces.getReplace().isEmpty()) {
            log.error("Log desensitization is enabled, but desensitization rules are not configured");
            return msg;
        }

        String sensitiveMsg = msg;
        for (SensitiveRegexReplacement replace : this.replaces.getReplace()) {
            sensitiveMsg = replace.format(sensitiveMsg);
        }
        return sensitiveMsg;
    }
}