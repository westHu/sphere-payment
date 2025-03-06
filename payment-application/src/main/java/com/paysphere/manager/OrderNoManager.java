package com.paysphere.manager;

import cn.hutool.core.util.RandomUtil;
import com.github.f4b6a3.ulid.UlidCreator;
import com.paysphere.cache.RedisService;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.paysphere.TradeConstant.DF_2;

@Slf4j
@Component
public class OrderNoManager {

    @Resource
    RedisService redisService;

    /**
     * 生成业务单号
     */
    public String getBusinessNo() {
        return UlidCreator.getUlid().toString();
    }

    /**
     * 生成交易单号 [区域 + 交易类型 + 商户号 + 时间戳 + 随机数] 24位
     */
    public String getTradeNo(Integer area, TradeTypeEnum tradeTypeEnum, String merchantId) {
        if (Objects.isNull(area) || Objects.isNull(tradeTypeEnum) || StringUtils.isBlank(merchantId)) {
            throw new PaymentException(ExceptionCode.ORDER_NUMBER_GENERATION_ERROR);
        }
        Integer tradeType = tradeTypeEnum.getCode();
        merchantId = merchantId.replace("sandbox-", "");
        String tradeNoPrefix = area + tradeType.toString() + merchantId + LocalDateTime.now().format(DF_2);

        //使用再试 + 缓存来唯一
        for (int i = 0; i < 10; i++) {
            int nextInt = RandomUtil.getSecureRandom().nextInt(10);
            String tradeNo =  tradeNoPrefix + nextInt;
            String redisKey = "CACHE_TRADE_NO" + tradeNo;

            //不存在并且成功为其设置了值返回true;  已存在返回 false
            boolean setIfAbsent = redisService.setIfAbsent(redisKey, tradeNo, 60);
            if (setIfAbsent) {
                return tradeNo;
            }
            log.warn("TradeNo repetition: {}", tradeNo);
        }
        throw new PaymentException(ExceptionCode.ORDER_NUMBER_GENERATION_BUSY);
    }


}
