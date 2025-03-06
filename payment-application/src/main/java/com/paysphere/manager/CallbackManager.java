package com.paysphere.manager;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.paysphere.command.dto.trade.callback.TradeCallBackDTO;
import com.paysphere.config.platform.PlatformKeyConfiguration;
import com.paysphere.config.platform.SandboxPlatformKeyConfiguration;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.paysphere.TradeConstant.DF_3;
import static com.paysphere.TradeConstant.SANDBOX;
import static com.paysphere.TradeConstant.UTF8;
import static com.paysphere.TradeConstant.X_SIGNATURE;
import static com.paysphere.TradeConstant.X_TIMESTAMP;
import static com.paysphere.TradeConstant.ZONE_ID;


@Slf4j
@Component
public class CallbackManager {

    @Resource
    PlatformKeyConfiguration configuration;
    @Resource
    SandboxPlatformKeyConfiguration sandboxConfiguration;

    /**
     * api callback
     */
    public String apiCallback(TradeCallBackDTO dto) {
        String tradeNo = dto.getBody().getTradeNo();
        log.info("apiCallback tradeNo ={}. dto={}", tradeNo, JSONUtil.toJsonStr(dto));
        String datetime = ZonedDateTime.of(LocalDateTime.now(), ZONE_ID).format(DF_3);
        String url = dto.getUrl();
        HttpRequest httpRequest = HttpUtil.createPost(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(X_TIMESTAMP, datetime)
                .header(X_SIGNATURE, buildSignature(dto, datetime))
                .body(JSONUtil.toJsonStr(dto.getBody()))
                .setConnectionTimeout(3000)  //设置连接超时时间
                .setReadTimeout(5000); //读取超时时间

        try (HttpResponse execute = httpRequest.execute()) {
            String response = execute.body();
            log.info("apiCallback tradeNo={} response ={}", tradeNo, response);

            //防止返回大字符串而进行截取
            if (StringUtils.isNotBlank(response) && response.length() > 32) {
                return response.substring(0, 32);
            }
            return response;
        } catch (Exception e) {
            log.error("apiCallback tradeNo={} exception={}", tradeNo, e.getMessage());
            return e.getMessage();
        }
    }

    //--------------------------------------------------------------------------------------------------------------
    /**
     * 加签, 此处是smiley发给商户，直接异常即可
     */
    private String buildSignature(TradeCallBackDTO dto, String datetime) {
        String tradeNo = dto.getBody().getTradeNo();
        String content = tradeNo + "|" + datetime;
        log.info("buildSignature content={}", content);
        String privateKey;
        String mode = dto.getMode();
        try {
            if (mode.equals(SANDBOX)) {
                privateKey = sandboxConfiguration.parseSandboxPrivateKey();
            } else {
                privateKey = configuration.parsePrivateKey();
            }
        } catch (Exception e) {
            log.error("callback signature private key error", e);
            throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "Callback signature error");
        }

        return RSAUtils.signToMerchant(content, privateKey, UTF8);
    }

}
