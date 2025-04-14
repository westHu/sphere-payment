package share.sphere.utils;

import share.sphere.TradeConstant;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.time.LocalDateTime;

import static share.sphere.TradeConstant.FILE_SUFFIX_CSV;
import static share.sphere.TradeConstant.PATH_EXPORT;
import static share.sphere.TradeConstant.PATH_SETTLEMENT;

public class StorageUtil {

    private StorageUtil() {
        throw new PaymentException("Utility classes should not have public constructors");
    }

    /**
     * 导出文件的命名规则
     */
    public static String exportCsvFile(String type) {
        return PATH_EXPORT + type + LocalDateTime.now().format(TradeConstant.DF_1) + FILE_SUFFIX_CSV;
    }

    /**
     * 结算文件的命名规则
     */
    public static String settlementCsvFile(String fileNameDate) {
        return PATH_SETTLEMENT
                + String.join("-", TradeConstant.FILE_NAME_TRADE, fileNameDate)
                + FILE_SUFFIX_CSV;
    }
}
