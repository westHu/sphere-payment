package com.paysphere.controller.request;

import lombok.Data;

@Data
public class EnterpriseAttributeReq {

    /**
     * logo JPG
     */
    private String imgLogo;

    /**
     * 董事的国民身证明 PDF
     */
    private String fileIdentification;

    /**
     * 公司税号文件 PDF
     */
    private String fileTax;

    /**
     * 商业识别号码/公司注册证/营业执照 PDF
     */
    private String fileBusinessLicense;

    /**
     * 公司契约 PDF
     */
    private String fileCorporateContract;

    /**
     * 最新修订契据 PDF
     */
    private String fileLatestRevisionContract;

    /**
     * 司法法令 PDF
     */
    private String fileJudicialDecree;

    /**
     * 股东结构 PDF
     */
    private String fileShareholderStructure;

    /**
     * 许可证 PDF
     */
    private String fileLicense;

    /**
     * 授权书 PDF
     */
    private String powerOfAttorney;

    /**
     * 护照 PDF
     */
    private String passport;

    /**
     * 授权书 PDF
     */
    private String limitedStayPermit;

    /**
     * 基金会注册证 PDF
     */
    private String foundationRegistrationCertificate;

    /**
     * 合作社登记证 PDF
     */
    private String cooperativeRegistrationCertificate;

    /**
     * 部长批准文件 PDF
     */
    private String ministerialApprovalDocuments;

    /**
     * 存折
     */
    private String fileBankBook;
}
