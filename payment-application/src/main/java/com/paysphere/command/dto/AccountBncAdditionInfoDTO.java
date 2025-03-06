package com.paysphere.command.dto;

import lombok.Data;

@Data
public class AccountBncAdditionInfoDTO {

    /**
     * PKP电子证书
     * pdf
     */
    private String spPkp;

    /**
     * 变更说明(书)
     * pdf
     */
    private String certificateAmendmentAct;

    /**
     * 修正案，修正條款
     * pdf
     */
    private String certificateDeedAmendment;

    /**
     * 证书建立
     * pdf
     */
    private String certificateEstablishment;

    /**
     * 证书公司注册
     * pdf
     */
    private String certificateIncorporation;

    /**
     * 证书最后修正
     * "qris/merchant/img/IpJadYAt9Lgh_l84Jab8e8OGei4YpoHHeuqUolZQYsI.pdf"
     */
    private String certificateLastAmendment;

    /**
     * 证书No40
     * "qris/merchant/img/nVJpafZbWN4LjtV-s4jIIlgBIzpbNf0a_sYh2alfpMU.xls"
     */
    private String certificateNo40;

    /**
     * 环境复制
     * "qris/merchant/img/njkOGkgFgBX9EHZaFK6aM-9CZ76kJBn01F7_veRggpo.jpeg"
     */
    private String environmentCopy;

}
