package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 地区枚举
 * 用于表示不同地区和数字货币类型
 *
 * @author West.Hu
 */
@Getter
@AllArgsConstructor
public enum RegionEnum {

    VIRTUAL("虚拟地区", "VC", "UTC"),

    CHINA("中国", "CN", "Asia/Shanghai"),
    USA("美国", "US", "America/New_York"),
    UK("英国", "GB", "Europe/London"),
    INDONESIA("印度尼西亚", "ID", "Asia/Jakarta"),
    THAILAND("泰国", "TH", "Asia/Bangkok"),
    INDIA("印度", "IN", "Asia/Kolkata"),
    BRAZIL("巴西", "BR", "America/Sao_Paulo"),
    MEXICO("墨西哥", "MX", "America/Mexico_City"),
    JAPAN("日本", "JP", "Asia/Tokyo"),
    SOUTH_KOREA("韩国", "KR", "Asia/Seoul"),
    SINGAPORE("新加坡", "SG", "Asia/Singapore"),
    MALAYSIA("马来西亚", "MY", "Asia/Kuala_Lumpur"),
    VIETNAM("越南", "VN", "Asia/Ho_Chi_Minh"),
    PHILIPPINES("菲律宾", "PH", "Asia/Manila");

    /**
     * 地区名称
     */
    private final String name;

    /**
     * 地区代码（ISO 3166-1 alpha-2）
     */
    private final String isoCode;

    /**
     * 时区ID
     */
    private final String zoneId;

    /**
     * 通过ISO代码获取地区枚举
     * 
     * @param isoCode ISO代码
     * @return 地区枚举,如果未找到则返回null
     */
    public static RegionEnum getByIsoCode(String isoCode) {
        if (Objects.isNull(isoCode)) {
            return VIRTUAL;
        }
        for (RegionEnum region : RegionEnum.values()) {
            if (region.getIsoCode().equals(isoCode)) {
                return region;
            }
        }
        return VIRTUAL;
    }
}