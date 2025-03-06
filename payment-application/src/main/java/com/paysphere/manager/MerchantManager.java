package com.paysphere.manager;

import cn.hutool.json.JSONUtil;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.db.entity.Merchant;
import com.paysphere.enums.MerchantQueryTypeEnum;
import com.paysphere.enums.MerchantStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.query.MerchantQueryService;
import com.paysphere.query.dto.MerchantTradeDTO;
import com.paysphere.query.param.OptionalMerchantDetailParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class MerchantManager {

    @Resource
    RedisService redisService;
    @Resource
    MerchantQueryService merchantQueryService;

    /**
     * 查询商户信息
     */
    public Merchant getMerchantBaseDTO(String merchantId) {
        // 先查询缓存
        String key = TradeConstant.CACHE_MERCHANT_BASE + merchantId;
        Object obj = redisService.get(key);
        Merchant merchant = Optional.ofNullable(obj).map(Object::toString)
                .map(e -> JSONUtil.toBean(e, Merchant.class))
                .orElse(null);
        log.info("getMerchantBaseDTO from cache key={}, merchantBaseDTO={}", key, JSONUtil.toJsonStr(merchant));

        if (Objects.isNull(merchant)) {
            merchant = merchantQueryService.getMerchant(merchantId);
            log.info("getMerchantBaseDTO from merchant merchantBaseDTO={}", JSONUtil.toJsonStr(merchant));
            if (Objects.isNull(merchant)) {
                log.error("getMerchantBaseDTO merchant not exist param={}", JSONUtil.toJsonStr(merchant));
                throw new PaymentException(ExceptionCode.MERCHANT_NOT_EXIST, merchantId);
            }
            redisService.set(key, JSONUtil.toJsonStr(merchant), 10 * 60); // FIX 根据业务调整
        }

        // 状态校验
        MerchantStatusEnum merchantStatusEnum = MerchantStatusEnum.codeToEnum(merchant.getStatus());
        log.info("getMerchantBaseDTO merchantStatusEnum={}", merchantStatusEnum.name());
        if (!MerchantStatusEnum.NORMAL.equals(merchantStatusEnum)) {
            log.error("getMerchantBaseDTO status invalid param={}", merchantId);
            throw new PaymentException(ExceptionCode.MERCHANT_STATUS_INVALID, merchantId);
        }

        return merchant;
    }


    /**
     * 查询商户信息
     */
    public MerchantTradeDTO getMerchantDTO(String merchantId, TradeTypeEnum tradeType,
                                              String paymentMethod, BigDecimal amount) {
        // 尝试从缓存获取, FIX 不加入金额，命中率高，但有一定概率会错误
        String key = TradeConstant.CACHE_TRADE_MERCHANT + merchantId + tradeType + paymentMethod;
        Object obj = redisService.get(key);
        MerchantTradeDTO merchantTradeDTO = Optional.ofNullable(obj).map(Object::toString)
                .map(e -> JSONUtil.toBean(e, MerchantTradeDTO.class))
                .orElse(null);
        log.info("getMerchantDTO from cache key={}, merchantTradeDTO={}", key, JSONUtil.toJsonStr(merchantTradeDTO));

        if (Objects.isNull(merchantTradeDTO)) {
            // 查询商户配置信息
            OptionalMerchantDetailParam param = new OptionalMerchantDetailParam();
            param.setTypeList(getMerchantQueryType(tradeType));
            param.setMerchantId(merchantId);
            param.setPaymentMethod(paymentMethod);
            param.setAmount(amount);
            merchantTradeDTO = merchantQueryService.getMerchantTradeDTO(param);
            log.info("getMerchantDTO from merchant merchantTradeDTO={}", JSONUtil.toJsonStr(merchantTradeDTO));
            if (Objects.isNull(merchantTradeDTO)) {
                log.error("getMerchantDTO merchant not exist param={}", JSONUtil.toJsonStr(param));
                throw new PaymentException(ExceptionCode.MERCHANT_NOT_EXIST, merchantId);
            }
            redisService.set(key, JSONUtil.toJsonStr(merchantTradeDTO), 60); // FIX 根据业务调整
        }

        // 校验基本信息  FIX 其实merchant已经校验
        Merchant merchant = merchantTradeDTO.getMerchant();
        if (Objects.isNull(merchant)) {
            log.error("getMerchantDTO merchantBaseDTO not exist param={}", JSONUtil.toJsonStr(merchant));
            throw new PaymentException(ExceptionCode.MERCHANT_NOT_EXIST, merchantId);
        }

        // 校验状态
        MerchantStatusEnum merchantStatusEnum = MerchantStatusEnum.codeToEnum(merchant.getStatus());
        if (!MerchantStatusEnum.NORMAL.equals(merchantStatusEnum)) {
            log.error("getMerchantDTO merchantStatus error. status={}", merchantStatusEnum);
            throw new PaymentException(ExceptionCode.MERCHANT_STATUS_INVALID, merchantId);
        }

        return merchantTradeDTO;
    }


    /**
     * 查询商户配置
     */
    public List<MerchantQueryTypeEnum> getMerchantQueryType(TradeTypeEnum typeEnum) {
        // 基本信息, 基本配置, 支付配置, 渠道配置
        if (typeEnum.equals(TradeTypeEnum.PAYMENT)) {
            return Arrays.asList(MerchantQueryTypeEnum.BASE, MerchantQueryTypeEnum.CONFIG, MerchantQueryTypeEnum.PAYMENT_CONFIG, MerchantQueryTypeEnum.PAYMENT_CHANNEL_CONFIG);
        } else if (typeEnum.equals(TradeTypeEnum.PAYOUT)) {
            return Arrays.asList(MerchantQueryTypeEnum.BASE, MerchantQueryTypeEnum.CONFIG, MerchantQueryTypeEnum.PAYOUT_CONFIG, MerchantQueryTypeEnum.PAYOUT_CHANNEL_CONFIG);
        }
        throw new PaymentException(ExceptionCode.UNSUPPORTED_QUERY_TYPE);
    }
}
