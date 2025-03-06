package com.paysphere.config;

import cn.hutool.core.lang.Assert;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "pay.trade")
public class TradeKeyConfig {

    /**
     * JKS证书
     */
    private String key;

    /**
     * JKS证书密码
     */
    private String trade;


    /**
     * 从配置文件解析私钥
     */
    public String parsePrivateKey() {
        Assert.notBlank(this.getKey(), () -> new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "Trade Private " +
                "key miss"));
        Assert.notBlank(this.getTrade(), () -> new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "Trade Private" +
                " key password miss"));
        try {
            byte[] decode = Base64.getDecoder().decode(this.getKey().getBytes());
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new ByteArrayInputStream(decode), null);

            String certAlias = this.getTrade().substring(8, 24);
            String certPassword = this.getTrade().substring(24, 40);

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(certAlias, certPassword.toCharArray());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            byte[] encoded = spec.getEncoded();
            return Base64.getEncoder().encodeToString(encoded);
        } catch (Exception e) {
            log.error("trade parse private key exception", e);
            throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "Trade parse private key exception");
        }
    }

}

