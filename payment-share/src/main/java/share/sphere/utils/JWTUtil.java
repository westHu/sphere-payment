package share.sphere.utils;

import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import share.sphere.utils.dto.JWTTokenMerchantDTO;
import share.sphere.utils.dto.JWTTokenOperatorDTO;
import share.sphere.utils.dto.JWTTokenUserDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 
 * 用于生成和校验JWT token
 * 包含商户和操作员信息
 */
@Slf4j
@Component
public class JWTUtil {

    /**
     * JWT密钥
     */
    private static final String secret = "sphere-payment-2024-secret-key-1234567890";

    /**
     * JWT过期时间(毫秒)
     */
    private static final long expire = 86400000;

    /**
     * 生成JWT token
     */
    public static String createToken(JWTTokenMerchantDTO merchantDTO,
                              JWTTokenOperatorDTO operatorDTO,
                              JWTTokenUserDTO userDTO) {
        // 创建签名器
        JWTSigner signer = JWTSignerUtil.hs256(secret.getBytes());
        
        // 设置payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("merchantDTO", JSONUtil.toJsonStr(merchantDTO));
        payload.put("operatorDTO", JSONUtil.toJsonStr(operatorDTO));
        payload.put("userDTO", JSONUtil.toJsonStr(userDTO));
        payload.put("expireTime", System.currentTimeMillis() + expire);
        
        // 生成token
        return cn.hutool.jwt.JWTUtil.createToken(payload, signer);
    }

    /**
     * 校验JWT token
     *
     * @param token JWT token
     * @return 校验结果
     */
    public static boolean verifyToken(String token) {
        try {
            // 验证token是否有效
            JWT jwt = cn.hutool.jwt.JWTUtil.parseToken(token);

            // 验证签名
            if (!jwt.verify(JWTSignerUtil.hs256(secret.getBytes()))) {
                log.error("JWT签名验证失败");
                return false;
            }

            // 验证是否过期
            JWTValidator.of(jwt).validateDate();

            return true;
        } catch (Exception e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }


    /**
     * 从token中解析商户信息
     *
     * @param token JWT token
     * @return 商户信息
     */
    public static JWTTokenMerchantDTO parseMerchant(String token) {
        JWT jwt = cn.hutool.jwt.JWTUtil.parseToken(token);
        return JSONUtil.toBean(jwt.getPayload("merchantDTO").toString(), JWTTokenMerchantDTO.class);
    }


    /**
     * 从token中解析操作员信息
     *
     * @param token JWT token
     * @return 操作员信息
     */
    public static JWTTokenOperatorDTO parseOperator(String token) {
        JWT jwt = cn.hutool.jwt.JWTUtil.parseToken(token);
        return JSONUtil.toBean(jwt.getPayload("operatorDTO").toString(), JWTTokenOperatorDTO.class);
    }

    /**
     * 从token中解析用户信息
     *
     * @param token JWT token
     * @return 用户信息
     */
    public static JWTTokenUserDTO parseUser(String token) {
        JWT jwt = cn.hutool.jwt.JWTUtil.parseToken(token);
        return JSONUtil.toBean(jwt.getPayload("userDTO").toString(), JWTTokenUserDTO.class);
    }


} 