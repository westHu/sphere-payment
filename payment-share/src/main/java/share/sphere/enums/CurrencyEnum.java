package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 货币枚举
 * 用于管理各类货币信息，包括法币和数字货币
 *
 * @author West.Hu
 */
@Getter
@AllArgsConstructor
public enum CurrencyEnum {

    // ============= 法币 =============
    // 主要法币 (1000-1999)
    CNY(1001, "人民币", "CNY", "¥", CurrencyType.FIAT),
    USD(1002, "美元", "USD", "$", CurrencyType.FIAT),
    EUR(1003, "欧元", "EUR", "€", CurrencyType.FIAT),
    GBP(1004, "英镑", "GBP", "£", CurrencyType.FIAT),
    JPY(1005, "日元", "JPY", "¥", CurrencyType.FIAT),
    KRW(1006, "韩元", "KRW", "₩", CurrencyType.FIAT),
    
    // 东南亚法币 (1100-1199)
    IDR(1101, "印尼盾", "IDR", "Rp", CurrencyType.FIAT),
    THB(1102, "泰铢", "THB", "฿", CurrencyType.FIAT),
    MYR(1103, "马来西亚林吉特", "MYR", "RM", CurrencyType.FIAT),
    SGD(1104, "新加坡元", "SGD", "S$", CurrencyType.FIAT),
    VND(1105, "越南盾", "VND", "₫", CurrencyType.FIAT),
    PHP(1106, "菲律宾比索", "PHP", "₱", CurrencyType.FIAT),
    
    // 南亚法币 (1200-1299)
    INR(1201, "印度卢比", "INR", "₹", CurrencyType.FIAT),
    PKR(1202, "巴基斯坦卢比", "PKR", "₨", CurrencyType.FIAT),
    BDT(1203, "孟加拉塔卡", "BDT", "৳", CurrencyType.FIAT),
    
    // 美洲法币 (1300-1399)
    CAD(1301, "加拿大元", "CAD", "C$", CurrencyType.FIAT),
    MXN(1302, "墨西哥比索", "MXN", "$", CurrencyType.FIAT),
    BRL(1303, "巴西雷亚尔", "BRL", "R$", CurrencyType.FIAT),
    ARS(1304, "阿根廷比索", "ARS", "$", CurrencyType.FIAT),
    
    // 大洋洲法币 (1400-1499)
    AUD(1401, "澳大利亚元", "AUD", "A$", CurrencyType.FIAT),
    NZD(1402, "新西兰元", "NZD", "NZ$", CurrencyType.FIAT),
    
    // 非洲法币 (1500-1599)
    ZAR(1501, "南非兰特", "ZAR", "R", CurrencyType.FIAT),
    NGN(1502, "尼日利亚奈拉", "NGN", "₦", CurrencyType.FIAT),
    EGP(1503, "埃及镑", "EGP", "E£", CurrencyType.FIAT),
    KES(1504, "肯尼亚先令", "KES", "KSh", CurrencyType.FIAT),
    
    // 中东法币 (1600-1699)
    AED(1601, "阿联酋迪拉姆", "AED", "د.إ", CurrencyType.FIAT),
    SAR(1602, "沙特里亚尔", "SAR", "﷼", CurrencyType.FIAT),
    TRY(1603, "土耳其里拉", "TRY", "₺", CurrencyType.FIAT),
    ILS(1604, "以色列新谢克尔", "ILS", "₪", CurrencyType.FIAT),

    // ============= 数字货币 =============
    // 主流数字货币 (2000-2099)
    BTC(2001, "比特币", "BTC", "₿", CurrencyType.CRYPTO),
    ETH(2002, "以太坊", "ETH", "Ξ", CurrencyType.CRYPTO),
    BNB(2003, "币安币", "BNB", "BNB", CurrencyType.CRYPTO),
    SOL(2004, "索拉纳", "SOL", "◎", CurrencyType.CRYPTO),
    DOT(2005, "波卡", "DOT", "DOT", CurrencyType.CRYPTO),
    ADA(2006, "艾达币", "ADA", "₳", CurrencyType.CRYPTO),
    
    // 稳定币 (2100-2199)
    USDT(2101, "泰达币", "USDT", "USDT", CurrencyType.CRYPTO),
    USDC(2102, "USD Coin", "USDC", "USDC", CurrencyType.CRYPTO),
    DAI(2103, "Dai", "DAI", "DAI", CurrencyType.CRYPTO),
    BUSD(2104, "Binance USD", "BUSD", "BUSD", CurrencyType.CRYPTO),
    
    // DeFi代币 (2200-2299)
    UNI(2201, "Uniswap", "UNI", "UNI", CurrencyType.CRYPTO),
    AAVE(2202, "Aave", "AAVE", "AAVE", CurrencyType.CRYPTO),
    COMP(2203, "Compound", "COMP", "COMP", CurrencyType.CRYPTO),
    MKR(2204, "Maker", "MKR", "MKR", CurrencyType.CRYPTO),
    
    // 公链代币 (2300-2399)
    AVAX(2301, "Avalanche", "AVAX", "AVAX", CurrencyType.CRYPTO),
    MATIC(2302, "Polygon", "MATIC", "MATIC", CurrencyType.CRYPTO),
    FTM(2303, "Fantom", "FTM", "FTM", CurrencyType.CRYPTO),
    NEAR(2304, "NEAR Protocol", "NEAR", "NEAR", CurrencyType.CRYPTO),
    
    // 隐私币 (2400-2499)
    XMR(2401, "门罗币", "XMR", "ɱ", CurrencyType.CRYPTO),
    ZEC(2402, "Zcash", "ZEC", "ZEC", CurrencyType.CRYPTO),
    DASH(2403, "达世币", "DASH", "DASH", CurrencyType.CRYPTO);

    /**
     * 数字代码
     * 法币: 1000-1999
     * 数字货币: 2000-2999
     */
    private final Integer code;

    /**
     * 货币名称
     */
    private final String name;

    /**
     * 货币代码
     */
    private final String symbol;

    /**
     * 显示符号
     */
    private final String displaySymbol;

    /**
     * 货币类型
     */
    private final CurrencyType type;

    /**
     * 编码映射缓存
     */
    private static final Map<Integer, CurrencyEnum> CODE_MAP = new HashMap<>();
    private static final Map<String, CurrencyEnum> SYMBOL_MAP = new HashMap<>();
    
    static {
        for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
            CODE_MAP.put(currencyEnum.getCode(), currencyEnum);
            SYMBOL_MAP.put(currencyEnum.getSymbol(), currencyEnum);
        }
    }

    /**
     * 根据数字代码获取枚举值
     *
     * @param code 数字代码
     * @return 对应的枚举值，如果未找到则返回null
     */
    public static CurrencyEnum codeToEnum(Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return CODE_MAP.get(code);
    }
    
    /**
     * 根据货币代码获取枚举值
     *
     * @param symbol 货币代码
     * @return 对应的枚举值，如果未找到则返回null
     */
    public static CurrencyEnum symbolToEnum(String symbol) {
        if (Objects.isNull(symbol)) {
            return null;
        }
        return SYMBOL_MAP.get(symbol);
    }
    
    /**
     * 根据编码获取货币名称
     *
     * @param code 数字代码
     * @return 货币名称，如果未找到则返回"未知货币"
     */
    public static String codeToName(Integer code) {
        CurrencyEnum currencyEnum = codeToEnum(code);
        return currencyEnum != null ? currencyEnum.getName() : "未知货币";
    }
    
    /**
     * 格式化金额
     *
     * @param amount 金额
     * @return 格式化后的金额字符串
     */
    public String formatAmount(double amount) {
        if (this.type == CurrencyType.CRYPTO) {
            return String.format("%s %.8f", this.displaySymbol, amount);
        } else {
            return String.format("%s %.2f", this.displaySymbol, amount);
        }
    }

    /**
     * 货币类型枚举
     */
    public enum CurrencyType {
        FIAT,    // 法币
        CRYPTO   // 数字货币
    }
}