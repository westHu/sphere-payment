package app.sphere.manager;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 收银台管理器
 * 
 * 主要功能：
 * 1. 处理收银台支付结果的展示方式
 * 2. 管理不同支付方式的交互逻辑
 * 
 * 支付结果展示方式：
 * 1. DISPLAY - 在当前页面展示（如 VA、PIX、QRIS 等）
 * 2. REDIRECT - 需要跳转到外部页面
 * 
 * 使用场景：
 * 1. 收银台支付结果展示
 * 2. 支付方式交互控制
 * 3. 支付流程导航
 */
@Slf4j
@Component
public class CashierManager {

    /**
     * HTTP URL前缀
     */
    private static final String HTTP_PREFIX = "http";

    /**
     * 获取支付结果的操作展示方式
     * 
     * @param paymentResult 支付结果（可能是URL或其他数据）
     * @param paymentMethod 支付方式（QRIS、VA等）
     * @return 展示方式（DISPLAY/REDIRECT）
     */
    public String getMethodResultOptType(String paymentResult, String paymentMethod) {
        log.debug("[收银台] 处理支付结果展示方式, paymentMethod={}, paymentResult={}", paymentMethod, paymentResult);

        // QRIS 支付方式固定使用 DISPLAY 模式
        if (StringUtils.equalsIgnoreCase("PAYMENT_METHOD_QRIS", paymentMethod)) {
            log.debug("[收银台] QRIS支付使用DISPLAY模式展示");
            return "DISPLAY";
        }

        // 处理重定向类型
        if (StringUtils.isNotBlank(paymentResult) && paymentResult.toLowerCase().startsWith(HTTP_PREFIX)) {
            log.debug("[收银台] 检测到HTTP URL，使用REDIRECT模式");
            return "REDIRECT";
        }

        // 默认使用 DISPLAY 模式
        log.debug("[收银台] 使用默认的DISPLAY模式展示");
        return "REDIRECT";
    }
}
