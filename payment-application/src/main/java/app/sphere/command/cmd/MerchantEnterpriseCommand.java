package app.sphere.command.cmd;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantEnterpriseCommand {

    /**
     * 0011
     * 商业需要 收款1、出款2 收款出款3
     */
    private Integer businessAction;

    /**
     * 公司名称
     */
    @NotBlank(message = "enterpriseName is required")
    private String enterpriseName;

    /**
     * 实体类型
     */
    @NotNull(message = "enterpriseType is required")
    private Integer enterpriseType;

    /**
     * 是否提供支付/金融服务
     */
    @NotNull(message = "financialBusiness is required")
    private Boolean financialBusiness;

    /**
     * 法人证件号
     */
    //@NotBlank(message = "legalCertificateNumber is required")
    private String legalCertificateNumber;

    /**
     * 税号
     */
    @NotBlank(message = "taxNumber is required")
    private String taxNumber;

    /**
     * 营业执照号
     */
    //@NotBlank(message = "businessLicenseNo is required")
    private String businessLicenseNo;

    /**
     * 品牌名称
     */
    @NotBlank(message = "brandName is required")
    private String brandName;

    /**
     * 行业ID
     */
    private String industryId;

    /**
     * 行业
     */
    @NotBlank(message = "industry is required")
    private String industry;

    /**
     * 产品/服务
     */
    @NotBlank(message = "product is required")
    private String product;

    /**
     * 是否网上销售商品/服务
     */
    @NotNull(message = "onlineSales is required")
    private Boolean onlineSales;

    /**
     * 网上销售商品/服务的链接
     */
    private String saleLink;

    /**
     * 业务描述
     */
    @NotBlank(message = "businessDescription is required")
    private String businessDescription;

    /**
     * 营业地点
     */
    @NotBlank(message = "businessAddress is required")
    private String businessAddress;

    /**
     * 负责人
     */
    private String pic;

    /**
     * 负责人手机
     */
    private String picPhone;

    /**
     * 扩展信息
     */
    @NotNull(message = "attribute is required")
    @Valid
    private EnterpriseAttributeCommand attribute;


}
