package api.sphere.job.param;

import lombok.Data;

@Data
public class SettleJobParam {

    private String batchNo;

    private String merchantId;

    private String startTradeTime;

    private String endTradeTime;
}
