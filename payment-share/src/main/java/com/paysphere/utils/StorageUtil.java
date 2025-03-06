package com.paysphere.utils;

import com.paysphere.TradeConstant;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;

import java.time.LocalDateTime;

import static com.paysphere.TradeConstant.FILE_SUFFIX_CSV;
import static com.paysphere.TradeConstant.PATH_EXPORT;
import static com.paysphere.TradeConstant.PATH_SETTLEMENT;

public class StorageUtil {

    private StorageUtil() {
        throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "Utility classes should not have public " +
                "constructors");
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
