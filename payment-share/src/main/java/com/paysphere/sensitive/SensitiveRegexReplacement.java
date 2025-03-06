package com.paysphere.sensitive;

import lombok.Data;

import java.util.regex.Pattern;

/**
 * RegexReplacement
 */
@Data
public class SensitiveRegexReplacement {
    /**
     * 脱敏匹配正则
     */
    private Pattern regex;

    /**
     * 替换正则
     */
    private String replacement;

    /**
     * Perform the replacement
     */
    public String format(final String msg) {
        return regex.matcher(msg).replaceAll(replacement);
    }

    public void setRegex(String regex) {
        this.regex = Pattern.compile(regex);
    }

}