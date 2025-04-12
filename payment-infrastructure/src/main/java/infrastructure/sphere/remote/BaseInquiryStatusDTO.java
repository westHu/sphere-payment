package infrastructure.sphere.remote;

import lombok.Data;

@Data
public class BaseInquiryStatusDTO {


    /**
     * 底层渠道的交易单号 eg: rrn utr
     */
    private String trxId;

    /**
     * 成功/失败
     */
    private Boolean status;

}
