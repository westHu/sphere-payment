package infrastructure.sphere.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Google Authenticator配置类
 * 
 * 用于生成和验证Google Authenticator动态口令
 * 支持:
 * 1. 生成密钥
 * 2. 生成二维码内容
 * 3. 验证动态口令
 */
@Slf4j
public class GoogleAuthenticator {

    /**
     * 密钥长度
     */
    public static final int SECRET_SIZE = 10;

    /**
     * 随机数种子
     */
    public static final String SEED = RandomStringUtils.randomAlphanumeric(64);

    /**
     * 随机数算法
     */
    public static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";

    /**
     * 窗口大小
     */
    private int window_size = 3;

    /**
     * 验证动态口令
     * 
     * @param codes 动态口令
     * @param savedSecret 保存的密钥
     * @return 验证结果
     */
    public static Boolean verifyCode(String codes, String savedSecret) {
        try {
            long code = Long.parseLong(codes);
            long t = System.currentTimeMillis();
            GoogleAuthenticator ga = new GoogleAuthenticator();
            return ga.check_code(savedSecret, code, t);
        } catch (Exception e) {
            log.error("验证动态口令失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成二维码URL
     * 
     * @return 二维码URL
     */
    public static String genURL() {
        try {
            String secret = GoogleAuthenticator.generateSecretKey();
            return GoogleAuthenticator.getQRBarcodeURL("WhooshPay", "Indonesia", secret);
        } catch (Exception e) {
            log.error("生成二维码URL失败: {}", e.getMessage());
            throw new RuntimeException("生成二维码URL失败", e);
        }
    }

    /**
     * 生成二维码URL
     * 
     * @param secret 密钥
     * @return 二维码URL
     */
    public static String genURL(String secret) {
        try {
            return GoogleAuthenticator.getQRBarcodeURL("WhooshPay", "Indonesia", secret);
        } catch (Exception e) {
            log.error("生成二维码URL失败: {}", e.getMessage());
            throw new RuntimeException("生成二维码URL失败", e);
        }
    }

    /**
     * 生成密钥
     * 
     * @return 密钥
     */
    public static String generateSecretKey() {
        try {
            SecureRandom sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
            sr.setSeed(Base64.decodeBase64(SEED));
            byte[] buffer = sr.generateSeed(SECRET_SIZE);
            Base32 codec = new Base32();
            byte[] bEncodedKey = codec.encode(buffer);
            return new String(bEncodedKey);
        } catch (NoSuchAlgorithmException e) {
            log.error("生成密钥失败: {}", e.getMessage());
            throw new RuntimeException("生成密钥失败", e);
        }
    }

    /**
     * 生成二维码内容
     * 
     * @param user 用户
     * @param host 主机
     * @param secret 密钥
     * @return 二维码内容
     */
    public static String getQRBarcodeURL(String user, String host, String secret) {
        try {
            String format = "https://quickchart.io/chart?chs=400x400&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s" +
                    "%%3Fsecret%%3D%s";
            return String.format(format, user, host, secret);
        } catch (Exception e) {
            log.error("生成二维码内容失败: {}", e.getMessage());
            throw new RuntimeException("生成二维码内容失败", e);
        }
    }

    /**
     * 验证动态口令
     * 
     * @param key 密钥
     * @param t 时间戳
     * @return 动态口令
     */
    private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }

    /**
     * 检查动态口令
     * 
     * @param secret 密钥
     * @param code 动态口令
     * @param timeMsec 时间戳
     * @return 检查结果
     */
    public boolean check_code(String secret, long code, long timeMsec) {
        try {
            Base32 codec = new Base32();
            byte[] decodedKey = codec.decode(secret);
            long t = (timeMsec / 1000L) / 30L;
            for (int i = -window_size; i <= window_size; ++i) {
                long hash = verify_code(decodedKey, t + i);
                if (hash == code) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("检查动态口令失败: {}", e.getMessage());
            return false;
        }
    }
}
 
 
 