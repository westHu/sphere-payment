package app.sphere.command.cmd;

import lombok.Data;

@Data
public class EnterpriseAttributeCommand {

    /**
     * logo JPG
     */
    //@NotBlank(message = "imgLogo is required")
    private String imgLogo;

    /**
     * 董事的国民身证明 PDF
     */
    //@NotBlank(message = "fileIdentification is required")
    private String fileIdentification;

    /**
     * 公司税号文件 PDF
     */
    //@NotBlank(message = "fileTax is required")
    private String fileTax;

    /**
     * 商业识别号码/公司注册证/营业执照 PDF
     */
    //@NotBlank(message = "fileBusinessLicense is required")
    private String fileBusinessLicense;

    /**
     * 公司契约 PDF
     */
    //@NotBlank(message = "fileCorporateContract is required")
    private String fileCorporateContract;

    /**
     * 最新修订契据 PDF
     */
    //@NotBlank(message = "fileLatestRevisionContract is required")
    private String fileLatestRevisionContract;

    /**
     * 司法法令 PDF
     */
    //@NotBlank(message = "fileJudicialDecree is required")
    private String fileJudicialDecree;

    /**
     * 股东结构 PDF
     */
    //@NotBlank(message = "fileShareholderStructure is required")
    private String fileShareholderStructure;

    /**
     * 许可证 PDF
     */
    //@NotBlank(message = "fileLicense is required")
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
