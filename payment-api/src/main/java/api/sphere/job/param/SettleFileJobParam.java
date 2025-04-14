package api.sphere.job.param;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SettleFileJobParam {

    /**
     * 交易时间
     */
    private String tradeDate = LocalDate.now().plusDays(-1).toString();

}
