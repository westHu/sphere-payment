package share.sphere.sensitive;

import java.util.ArrayList;
import java.util.List;

/**
 * Sensitive
 */
public class SensitiveLogbackReplaces {

    /**
     * 脱敏正则列表
     */
    private List<SensitiveRegexReplacement> replace = new ArrayList<>();

    /**
     * 添加规则（因为replace类型是list，必须指定addReplace方法用以添加多个）
     */
    public void addReplace(SensitiveRegexReplacement replacement) {
        replace.add(replacement);
    }

    public List<SensitiveRegexReplacement> getReplace() {
        return replace;
    }

    public void setReplace(List<SensitiveRegexReplacement> replace) {
        this.replace = replace;
    }
}