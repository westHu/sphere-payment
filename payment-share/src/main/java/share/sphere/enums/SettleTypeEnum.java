package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SettleTypeEnum {

    // ============== 实时结算 ==============
    /**
     * 实时结算
     * 支付成功后立即结算
     */
    T0("T+0", "实时结算", "支付成功后立即结算"),

    // ============== 次日结算 ==============
    /**
     * 次日结算（工作日）
     * 支付成功后下一个工作日结算
     */
    T1("T+1", "次日结算(工作日)", "支付成功后下一个工作日结算"),

    /**
     * 次日结算（自然日）
     * 支付成功后次日结算，不考虑是否为工作日
     */
    D1("D+1", "次日结算(自然日)", "支付成功后次日结算，不考虑是否为工作日"),

    // ============== 隔日结算 ==============
    /**
     * 隔日结算（工作日）
     * 支付成功后隔一个工作日结算
     */
    T2("T+2", "隔日结算(工作日)", "支付成功后隔一个工作日结算"),

    /**
     * 隔日结算（自然日）
     * 支付成功后隔日结算，不考虑是否为工作日
     */
    D2("D+2", "隔日结算(自然日)", "支付成功后隔日结算，不考虑是否为工作日"),

    // ============== 自定义结算 ==============
    /**
     * 自定义结算
     * 根据配置的时间点结算
     */
    CUSTOM("CUSTOM", "自定义结算", "根据配置的时间点结算");

    /**
     * 结算类型编码
     */
    private final String code;

    /**
     * 结算类型名称
     */
    private final String name;

    /**
     * 结算类型描述
     */
    private final String description;

    /**
     * 根据编码获取枚举值
     *
     * @param code 结算类型编码
     * @return 对应的枚举值，如果不存在则返回null
     */
    public static SettleTypeEnum getByCode(String code) {
        for (SettleTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 判断编码是否存在
     *
     * @param code 结算类型编码
     * @return 是否存在
     */
    public static boolean contains(String code) {
        return getByCode(code) != null;
    }

    /**
     * 判断是否为工作日结算类型
     *
     * @return 是否为工作日结算类型
     */
    public boolean isWorkdaySettle() {
        return this.code.startsWith("T");
    }

    /**
     * 判断是否为自然日结算类型
     *
     * @return 是否为自然日结算类型
     */
    public boolean isNaturalSettle() {
        return this.code.startsWith("D");
    }

}
