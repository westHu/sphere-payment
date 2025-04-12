package share.sphere;

import share.sphere.exception.PaymentException;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TradeConstant {

    private TradeConstant() {
        throw new PaymentException("Utility classes should not have public constructors");
    }

    public static final String SYSTEM = "system";
    public static final String AMOUNT_MAX = "1000000000";

    /**
     * sql
     */
    public static final int INIT_VERSION = 0;
    public static final String LIMIT_1 = "limit 1";
    public static final String VERSION_SQL = "version = version + 1";
    public static final String CALLBACK_SQL = "call_back_times = call_back_times + 1";

    /**
     * trade 24H
     */
    public static final Integer TRADE_EXPIRY_PERIOD_MAX = 24 * 60 * 60;


    /**
     * job
     */
    public static final String JOB_NAME_TRADE_FILE = "paysphereTradeFileHandler";
    public static final String JOB_NAME_TRADE_PAY_TIMEOUT = "paysphereTradePayOrderTimeOutHandler";
    public static final String JOB_NAME_TRADE_STATISTICS = "paysphereTradeStatisticsHandler";


    /**
     * zoned
     */
    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Jakarta");

    /**
     * dateTimeFormatter
     */
    public static final String FORMATTER_0 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMATTER_1 = "yyyyMMddHHmmss";
    public static final String FORMATTER_2 = "yyMMddHHmmssSSS";
    public static final String FORMATTER_3 = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final DateTimeFormatter DF_0 = DateTimeFormatter.ofPattern(FORMATTER_0);
    public static final DateTimeFormatter DF_1 = DateTimeFormatter.ofPattern(FORMATTER_1);
    public static final DateTimeFormatter DF_2 = DateTimeFormatter.ofPattern(FORMATTER_2);
    public static final DateTimeFormatter DF_3 = DateTimeFormatter.ofPattern(FORMATTER_3);

    /**
     * paymentLink
     */
    public static final String PAYMENT_LINK_EMAIL_SUFFIX = "@paysphere.com";
    public static final String PAYMENT_LINK_REDIRECT = "https:// pay.payspherepay.id";

    /**
     * cashier
     */
    public static final String EXPERIENCE_CASHIER_PREFIX = "T";
    public static final String EXPERIENCE_CASHIER_DANA_URL = "https:// m.sandbox.dana.id/d/ipg/inputphone?phoneNumber=&ipgForwardUrl=d%2F";
    /**
     * callback
     */
    public static final String CALLBACK_POST_SUCCESS = "SUCCESS";

    /**
     * recharge
     */
    public static final String RECHARGE_BNC = "BNC";
    public static final String RECHARGE_BNC_ACCOUNT = "88880000237016";
    public static final String RECHARGE_BNC_HOLDER_NAME = "PT Bina Putra Sadaya";

    /**
     * check error
     */
    public static final String ERROR_TO_CHECK = "Please contact customer service for the reason of the error";
    public static final String ERROR_SPLIT = "error=";

    /**
     * settlement
     */
    public static final String SETTLEMENT_TRANSFER = "SETTLEMENT-TRANSFER";
    public static final String SETTLEMENT_WITHDRAW = "SETTLEMENT-WITHDRAW";

    /**
     * storage
     */
    public static final String PATH_EXPORT = "export/";
    public static final String PATH_SETTLEMENT = "settlement/";
    public static final String CONTENT_TYPE_CSV = "text/csv";
    public static final String FILE_SUFFIX_CSV = ".csv";
    public static final String FILE_NAME_TRADE = "trade";
    public static final String FILE_DEFAULT_FILED = "OK";

    /**
     * callback
     */
    public static final String SANDBOX = "sandbox";
    public static final String X_TIMESTAMP = "X-TIMESTAMP";
    public static final String X_SIGNATURE = "X-SIGNATURE";
    public static final String UTF8 = "UTF-8";


    /**
     * socket
     */
    public static final String SOCKET_TIME_OUT = "SocketTimeout";
    public static final String SOCKET_UNKNOWN = "SocketUnknown";

    /**
     * job
     */
    public static final String JOB_NAME_ACCOUNT_DAILY_SNAPSHOT = "settle_accountDailySnapshotHandler";
    public static final String JOB_NAME_ACCOUNT_FLOW_REVISION = "settle_accountFlowRevisionHandler";
    public static final String JOB_NAME_D1_PAY_SETTLE = "settle_d1SettleOrder4PayHandler";
    public static final String JOB_NAME_D2_PAY_SETTLE = "settle_d2SettleOrder4PayHandler";
    public static final String JOB_NAME_T1_PAY_SETTLE = "settle_t1SettleOrder4PayHandler";
    public static final String JOB_NAME_T2_PAY_SETTLE = "settle_t2SettleOrder4PayHandler";
    public static final String JOB_NAME_SETTLE_FILE = "settle_settleFileHandler";

}
