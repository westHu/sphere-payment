package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结算时间枚举
 * 定义每日的结算时间点
 */
@Getter
@AllArgsConstructor
public enum SettleTimeEnum {

    // ============== 凌晨结算 ==============
    /**
     * 凌晨0点结算
     */
    TIME_00("00:00", "凌晨结算", "每日凌晨0点结算"),

    // ============== 凌晨结算 ==============
    /**
     * 凌晨3点结算
     */
    TIME_03("03:00", "凌晨结算", "每日凌晨3点结算"),

    // ============== 早上结算 ==============
    /**
     * 早上6点结算
     */
    TIME_06("06:00", "早上结算", "每日早上6点结算"),

    /**
     * 早上9点结算
     */
    TIME_09("09:00", "早上结算", "每日早上9点结算"),

    // ============== 下午结算 ==============
    /**
     * 下午18点结算
     */
    TIME_18("18:00", "下午结算", "每日下午18点结算"),

    /**
     * 晚上22点结算
     */
    TIME_22("22:00", "晚上结算", "每日晚上22点结算");

    /**
     * 结算时间点
     * 格式：HH:mm
     */
    private final String time;

    /**
     * 结算时间段名称
     */
    private final String name;

    /**
     * 结算时间描述
     */
    private final String description;

    /**
     * 根据时间点获取枚举值
     *
     * @param time 结算时间点，格式：HH:mm
     * @return 对应的枚举值，如果不存在则返回null
     */
    public static SettleTimeEnum getByTime(String time) {
        for (SettleTimeEnum value : values()) {
            if (value.getTime().equals(time)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 判断时间点是否存在
     *
     * @param time 结算时间点，格式：HH:mm
     * @return 是否存在
     */
    public static boolean contains(String time) {
        return getByTime(time) != null;
    }

    /**
     * 获取下一个结算时间点
     *
     * @return 下一个结算时间点，如果当前是最后一个则返回第一个
     */
    public SettleTimeEnum next() {
        SettleTimeEnum[] values = values();
        int nextOrdinal = (this.ordinal() + 1) % values.length;
        return values[nextOrdinal];
    }

    /**
     * 获取上一个结算时间点
     *
     * @return 上一个结算时间点，如果当前是第一个则返回最后一个
     */
    public SettleTimeEnum previous() {
        SettleTimeEnum[] values = values();
        int prevOrdinal = (this.ordinal() - 1 + values.length) % values.length;
        return values[prevOrdinal];
    }
}