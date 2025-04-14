package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Stream;

/**
 * 交易状态
 */
@Getter
@AllArgsConstructor
public enum TradeStatusEnum {

    TRADE_INIT(0, "初始化", "INIT"),
    TRADE_SUCCESS(1, "交易成功", "SUCCESS"),
    TRADE_FAILED(-1, "交易失败", "FAILED"),

    TRADE_REVIEW(10, "审核中", "REVIEW"),
    TRADE_EXPIRED(-2, "已过期", "EXPIRED"),
    TRADE_CANCEL(-3, "已取消", "CANCEL"),

    UNKNOWN(99, "未知", "UNKNOWN");

    private final Integer code;
    private final String name;
    private final String merchantStatus;

    /**
     * 终态
     */
    public static List<Integer> getFinalStatus() {
        return Stream.of(TradeStatusEnum.TRADE_SUCCESS, TradeStatusEnum.TRADE_FAILED)
                .map(TradeStatusEnum::getCode)
                .toList();
    }

    /**
     * 终态
     */
    public static List<String> getFinalMerchantStatus() {
        return Stream.of(TradeStatusEnum.TRADE_SUCCESS, TradeStatusEnum.TRADE_FAILED)
                .map(TradeStatusEnum::getMerchantStatus)
                .toList();
    }


    /**
     * getTradeStatusEnum
     */
    public static TradeStatusEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return UNKNOWN;
        }
        return Arrays.stream(TradeStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }


}
