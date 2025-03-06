package com.paysphere.mq.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TgTemplateEnum {

    TRADE_REVIEW("交易订单审核通知"),
    CHANNEL_BALANCE("渠道余额通知"),
    RECONCILE_NOTIFY("对账结果通知"),
    JOB_ALARM("任务监控"),
    MERCHANT_REG_NOTIFY("商户注册通知"),
    MERCHANT_KYC_NOTIFY("商户KYC通知"),
    MERCHANT_REVIEW_NOTIFY("商户二次进件通知"),
    TRADE_REVIEW_NOTIFY("交易订单审核通知"),
    CHANNEL_MONITOR("渠道监控"),

    UNKNOWN("未知类型");

    private final String desc;

    public static TgTemplateEnum codeToEnum(String name) {
        if (Objects.isNull(name)) {
            return UNKNOWN;
        }
        return Arrays.stream(TgTemplateEnum.values())
                .filter(e -> e.name().equals(name))
                .findAny().orElse(UNKNOWN);
    }

}
