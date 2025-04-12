package api.sphere.job.param;

import lombok.Data;

import java.time.LocalDate;


@Data
public class TradeFileJobParam {

    /**
     * 交易日期
     */
    private String tradeDate = LocalDate.now().plusDays(-1).toString();

    /**
     * 渠道名称
     */
    private String channelName;

}
