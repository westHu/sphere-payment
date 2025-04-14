package api.sphere.controller.response;

import lombok.Data;

@Data
public class MEnterpriseVO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 公司名称
     */
    private String enterpriseName;

    /**
     * 实体类型
     */
    private Integer enterpriseType;

    /**
     * 是否提供支付/金融服务
     */
    private boolean financialBusiness;

    /**
     * 法人证件号
     */
    private String legalCertificateNumber;

    /**
     * 税号
     */
    private String taxNumber;

    /**
     * 营业执照号
     */
    private String businessLicenseNo;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 行业
     */
    private String industry;

    /**
     * 产品/服务
     */
    private String product;

    /**
     * 是否网上销售商品/服务
     */
    private boolean onlineSales;

    /**
     * 网上销售商品/服务的链接
     */
    private String saleLink;

    /**
     * 业务描述
     */
    private String businessDescription;

    /**
     * 营业地点
     */
    private String businessAddress;

    /**
     * 扩展信息
     */
    private String attribute;


}
