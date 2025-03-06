package com.paysphere.query.dto;

import lombok.Data;


@Data
public class MerchantSettlementFileDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 结算日期
     */
    private String settleDate;

    /**
     * 结算文件名称
     */
    private String fileName;

    /**
     * 结算文件路径
     */
    private String filePath;


}
