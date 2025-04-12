package app.sphere.manager;

import cn.hutool.core.util.RandomUtil;
import share.sphere.enums.CurrencyEnum;
import share.sphere.enums.RegionEnum;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static share.sphere.TradeConstant.DF_2;

@Slf4j
@Component
public class OrderNoManager {

    /**
     * 平台号
     */
    public String getMerchantId(Long id) {
        long base = 100000000 + id;
        return String.valueOf(base);
    }

    /**
     * 商户账户号
     */
    public String getAccountNo(String currency, String merchantId) {
        return CurrencyEnum.valueOf(currency).name() + "-" + merchantId;
    }

    /**
     * 平台号
     */
    public String getPlatformId() {
        return "00000001";
    }

    /**
     * 平台名称
     */
    public String getPlatformName() {
        return "SPHERE01";
    }
    /**
     * 平台账户号
     */
    public String getPlatformAccountNo(String currency) {
        return CurrencyEnum.valueOf(currency).name() + "-00000001";
    }

    /**
     * 地区代码 + 交易类型 + 商户号 + 时间格式 +
     * TT + YYMMDD + HHMMSS + RRR + CCC + MMMMMM + NNNNN + RRR
     * 生成交易单号 [区域 + 交易类型 + 商户号 + 时间戳 + 随机数] 24位
     */
    public String getTradeNo(String region, TradeTypeEnum tradeTypeEnum, String merchantId) {
        if (Objects.isNull(region) || Objects.isNull(tradeTypeEnum) || StringUtils.isBlank(merchantId)) {
            throw new PaymentException(ExceptionCode.SYSTEM_BUSY);
        }
        Integer tradeType = tradeTypeEnum.getCode();
        merchantId = merchantId.replace("sandbox-", "");
        String tradeNoPrefix = region + tradeType.toString() + merchantId + LocalDateTime.now().format(DF_2);

        int nextInt = RandomUtil.getSecureRandom().nextInt(100);
        return tradeNoPrefix + nextInt;
    }


    /**
     * 订单时间戳转时间
     */
    public String getFormatDateTime(long timestamp, String region) {
        RegionEnum regionEnum = RegionEnum.getByIsoCode(region);
        return getFormatDateTime(timestamp, regionEnum.getZoneId(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 订单时间戳转时间
     */
    public String getFormatDateTime(long timestamp, String timeZone, String format) {
        if (StringUtils.isBlank(timeZone)) {
            timeZone = "America/New_York";
        }
        if (StringUtils.isBlank(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        // 从时间戳创建一个 Instant 对象
        // 使用指定的时区将 Instant 转换为 ZonedDateTime
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(timeZone));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return zonedDateTime.format(formatter);
    }

}
