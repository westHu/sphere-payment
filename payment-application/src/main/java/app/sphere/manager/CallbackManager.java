package app.sphere.manager;

import app.sphere.command.dto.trade.callback.TradeCallBackDTO;
import cn.hutool.http.*;
import cn.hutool.json.JSONUtil;
import infrastructure.sphere.config.platform.PlatformKeyConfiguration;
import infrastructure.sphere.config.platform.SandboxPlatformKeyConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import share.sphere.exception.PaymentException;
import share.sphere.utils.RSAUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static share.sphere.TradeConstant.*;


/**
 * 商户回调管理器
 * 负责处理交易完成后的商户通知，支持沙箱和生产环境
 */
@Slf4j
@Component
public class CallbackManager {

    /**
     * 回调请求配置
     */
    private static final int CONNECTION_TIMEOUT = 3000; // 连接超时时间（毫秒）
    private static final int READ_TIMEOUT = 5000;       // 读取超时时间（毫秒）
    private static final int MAX_RESPONSE_LENGTH = 32;  // 最大响应长度

    @Resource
    PlatformKeyConfiguration configuration;
    @Resource
    SandboxPlatformKeyConfiguration sandboxConfiguration;

    /**
     * 执行API回调通知
     * 
     * @param dto 回调数据传输对象
     * @return 回调结果
     */
    public String apiCallback(TradeCallBackDTO dto) {
        String tradeNo = dto.getBody().getTradeNo();
        log.info("[Callback] 开始处理订单回调, tradeNo={}", tradeNo);
        
        // 构建回调时间
        String datetime = ZonedDateTime.of(LocalDateTime.now(), ZONE_ID).format(DF_3);
        
        // 构建回调请求
        HttpRequest httpRequest = buildCallbackRequest(dto, datetime);
        
        try (HttpResponse response = httpRequest.execute()) {
            String responseBody = response.body();
            log.info("[Callback] 订单回调完成, tradeNo={}, response={}", tradeNo, responseBody);
            
            return truncateResponseIfNeeded(responseBody);
        } catch (Exception e) {
            log.error("[Callback] 订单回调异常, tradeNo={}, error={}", tradeNo, e.getMessage());
            return e.getMessage();
        }
    }

    /**
     * 构建回调HTTP请求
     */
    private HttpRequest buildCallbackRequest(TradeCallBackDTO dto, String datetime) {
        return HttpUtil.createPost(dto.getUrl())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(X_TIMESTAMP, datetime)
                .header(X_SIGNATURE, buildSignature(dto, datetime))
                .body(JSONUtil.toJsonStr(dto.getBody()))
                .setConnectionTimeout(CONNECTION_TIMEOUT)
                .setReadTimeout(READ_TIMEOUT);
    }

    /**
     * 构建回调签名
     * 使用RSA私钥对(tradeNo|timestamp)进行签名
     */
    private String buildSignature(TradeCallBackDTO dto, String datetime) {
        String tradeNo = dto.getBody().getTradeNo();
        String content = String.format("%s|%s", tradeNo, datetime);
        log.info("[Callback] 构建签名内容: {}", content);

        try {
            String privateKey = getPrivateKey(dto.getMode());
            String signature = RSAUtils.signToMerchant(content, privateKey, UTF8);
            log.debug("[Callback] 签名结果: {}", signature);
            return signature;
        } catch (Exception e) {
            log.error("[Callback] 签名生成失败, tradeNo={}", tradeNo, e);
            throw new PaymentException("Callback Signature Error");
        }
    }

    /**
     * 根据模式获取私钥
     */
    private String getPrivateKey(String mode) throws Exception {
        return mode.equals(SANDBOX) ? 
            sandboxConfiguration.parseSandboxPrivateKey() : 
            configuration.parsePrivateKey();
    }

    /**
     * 截断过长的响应内容
     */
    private String truncateResponseIfNeeded(String response) {
        if (StringUtils.isNotBlank(response) && response.length() > MAX_RESPONSE_LENGTH) {
            return response.substring(0, MAX_RESPONSE_LENGTH);
        }
        return response;
    }
}

