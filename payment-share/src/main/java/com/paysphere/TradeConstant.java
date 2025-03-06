package com.paysphere;

import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TradeConstant {

    public static final String IP = "ip";
    public static final String PROMETHEUS = "prometheus";
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
     * cache
     */
    public static final String CACHE_MERCHANT_BASE = "CACHE_MERCHANT_BASE:";
    public static final String CACHE_TRADE_MERCHANT = "CACHE_TRADE_MERCHANT:";
    public static final String CACHE_LINK_SETTING_STYLE = "CACHE_LINK_SETTING_STYLE:";
    public static final String CACHE_TRANSACTION_PAYMENT = "CACHE_TRANSACTION_PAYMENT:";
    public static final String CACHE_MERCHANT_CASH_PAYMENT = "CACHE_MERCHANT_CASH_PAYMENT:";
    public static final String CACHE_MERCHANT_PAYMENT_SETTING = "CACHE_MERCHANT_PAYMENT_SETTING:";
    public static final String CACHE_ORDER_STATUS = "CACHE_ORDER_STATUS:";

    /**
     * lock
     */
    public static final String LOCK_PREFIX_PAY_API = "PAY_API:";
    public static final String LOCK_PREFIX_PAY_WOO = "PAY_WOO:";
    public static final String LOCK_PREFIX_PAY_LINK = "PAY_LINK:";
    public static final String LOCK_PREFIX_CASH = "CASH_PAI:";
    public static final String LOCK_PREFIX_BATCH_CASH = "BATCH_CASH:";
    public static final String LOCK_PREFIX_CASH_REVIEW = "CASH_REVIEW:";
    public static final String LOCK_PREFIX_CASH_SUPPLEMENT = "CASH_SUPPLEMENT:";
    public static final String LOCK_PREFIX_CASH_REFUND = "CASH_REFUND:";
    public static final String LOCK_PREFIX_CASHIER_PAY = "CASHIER_PAY:";
    public static final String LOCK_PREFIX_PAY_SUPPLEMENT = "PAY_SUPPLEMENT:";
    public static final String LOCK_PREFIX_PAY_REFUND = "PAY_REFUND:";
    public static final String LOCK_PREFIX_TRANSFER = "TRANSFER_API:";
    public static final String LOCK_PREFIX_TRANSFER_REVIEW = "TRANSFER_REVIEW:";
    public static final String LOCK_PREFIX_PAYMENT_LISTENER = "PAYMENT_LISTENER:";
    public static final String LOCK_PREFIX_SETTLE_LISTENER = "SETTLE_LISTENER:";
    public static final String LOCK_PREFIX_RECHARGE = "RECHARGE_API:";
    public static final String LOCK_PREFIX_WITHDRAW = "WITHDRAW_API:";
    public static final String LOCK_PREFIX_WITHDRAW_REVIEW = "WITHDRAW_REVIEW:";
    public static final String LOCK_PREFIX_WITHDRAW_CONFIG = "WITHDRAW_CONFIG:";
    /**
     * job
     */
    public static final String JOB_NAME_TRADE_FILE = "paysphereTradeFileHandler";
    public static final String JOB_NAME_TRADE_PAY_TIMEOUT = "paysphereTradePayOrderTimeOutHandler";
    public static final String JOB_NAME_TRADE_STATISTICS = "paysphereTradeStatisticsHandler";
    public static final String JOB_NAME_TRADE_ITEMIZE = "paysphereTradeItemizeHandler";

    /**
     * mq
     */
    public static final String TRADE_PAYMENT_FINISH_CONSUMER_GROUP = "PAYSPHERE_TRADE_PAYMENT_FINISH_CONSUMER_GROUP";
    // from payment
    public static final String PAYMENT_FINISH_TOPIC = "PAYSPHERE_PAYMENT_FINISH_TOPIC";
    public static final String TRADE_SETTLE_FINISH_CONSUMER_GROUP = "PAYSPHERE_TRADE_SETTLE_FINISH_CONSUMER_GROUP";
    // from trade
    public static final String SETTLE_FINISH_TOPIC = "PAYSPHERE_SETTLE_FINISH_TOPIC";
    public static final String TRADE_CALLBACK_CONSUMER_GROUP = "PAYSPHERE_TRADE_CALLBACK_CONSUMER_GROUP";
    public static final String TRADE_CALLBACK_TOPIC = "PAYSPHERE_TRADE_CALLBACK_TOPIC";
    public static final String EMAIL_MESSAGE_TOPIC = "PAYSPHERE_EMAIL_MESSAGE_TOPIC";
    // to settle
    public static final String SETTLE_PAY_TOPIC = "PAYSPHERE_SETTLE_PAY_TOPIC";
    // to settle
    public static final String SETTLE_CASH_TOPIC = "PAYSPHERE_SETTLE_CASH_TOPIC";
    // to settle
    public static final String TRANSFER_TOPIC = "PAYSPHERE_TRANSFER_TOPIC";
    // to settle
    public static final String RECHARGE_TOPIC = "PAYSPHERE_RECHARGE_TOPIC";
    // to settle
    public static final String WITHDRAW_TOPIC = "PAYSPHERE_WITHDRAW_TOPIC";
    // to settle
    public static final String TRADE_EXAMINE_TOPIC = "PAYSPHERE_TRADE_EXAMINE_TOPIC";
    // to utils
    public static final String LARK_SEND_MESSAGE_TOPIC = "PAYSPHERE_LARK_SEND_MESSAGE_TOPIC";
    // to utils
    public static final String TG_SEND_MESSAGE_TOPIC = "PAYSPHERE_PAY_UTILS_TG_SEND_MESSAGE_TOPIC";

    // to settle
    public static final String UNFROZEN_TOPIC = "PAYSPHERE_UNFROZEN_TOPIC";

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
     * cash
     */
    public static final String BATCH_CASH_PURPOSE = "paysphere cash";

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
     * agent
     */
    public static final String AGENT_ID_REGEX_1 = "\\d{5}";
    public static final String AGENT_ID_REGEX_2 = "\\d{5}-\\d{1}";


    /**
     * socket
     */
    public static final String SOCKET_TIME_OUT = "SocketTimeout";
    public static final String SOCKET_UNKNOWN = "SocketUnknown";


    /**
     * URL
     */
    public static final String URL_SETTLE = "lb:// paysphere-settle";
    public static final String URL_UTILS = "lb:// paysphere-utils";
    public static final String URL_MERCHANT = "lb:// paysphere-merchant";
    public static final String URL_PAYMENT = "lb:// paysphere-payment";

    /**
     * lock
     */
    public static final String LOCK_NAME_EMAIL_CODE = "EMAIL_CODE:";

    /**
     * job
     */
    public static final String JOB_NAME_ACCOUNT_DAILY_SNAPSHOT = "whoosh_accountDailySnapshotHandler";
    public static final String JOB_NAME_ACCOUNT_FLOW_REVISION = "whoosh_accountFlowRevisionHandler";
    public static final String JOB_NAME_FLOW_ORDER_REVISION = "whoosh_accountFlowOrderRevisionHandler";
    public static final String JOB_NAME_H8_PAY_SETTLE = "whoosh_h8SettleOrder4PayHandler";
    public static final String JOB_NAME_D0_PAY_SETTLE = "whoosh_d0SettleOrder4PayHandler";
    public static final String JOB_NAME_D1_PAY_SETTLE = "whoosh_d1SettleOrder4PayHandler";
    public static final String JOB_NAME_D2_PAY_SETTLE = "whoosh_d2SettleOrder4PayHandler";
    public static final String JOB_NAME_D3_PAY_SETTLE = "whoosh_d3SettleOrder4PayHandler";
    public static final String JOB_NAME_T0_PAY_SETTLE = "whoosh_t0SettleOrder4PayHandler";
    public static final String JOB_NAME_T1_PAY_SETTLE = "whoosh_t1SettleOrder4PayHandler";
    public static final String JOB_NAME_T2_PAY_SETTLE = "whoosh_t2SettleOrder4PayHandler";
    public static final String JOB_NAME_T3_PAY_SETTLE = "whoosh_t3SettleOrder4PayHandler";
    public static final String JOB_NAME_M1_PAY_SETTLE = "whoosh_m1SettleOrder4PayHandler";
    public static final String JOB_NAME_SETTLE_FILE = "whoosh_settleFileHandler";
    public static final String JOB_NAME_MERCHANT_SETTLE = "whoosh_merchantSettleHandler";
    public static final String JOB_NAME_MERCHANT_FIX_SETTLE = "whoosh_merchantSettleFixHandler";
    public static final String JOB_NAME_MANUAL_PAY_SETTLE = "whoosh_manualSettleOrder4PayHandler";

    /**
     * platform account
     */
    public static final String PLATFORM_MERCHANT_CODE_PADDING = "000";
    public static final String PLATFORM_MERCHANT_NAME_PADDING = "admin@whooshpay.id";
    public static final String PLATFORM_MERCHANT_ACCOUNT_PADDING = "0001";

    private TradeConstant() {
        throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "Utility classes should not have public " +
                "constructors");
    }
}
