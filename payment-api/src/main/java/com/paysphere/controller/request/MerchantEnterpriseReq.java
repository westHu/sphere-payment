package com.paysphere.controller.request;

import lombok.Data;

@Data
public class MerchantEnterpriseReq {

    /**
     * 0011
     * 商业需要 收款1、出款2 收款出款3
     */
    private Integer businessAction;

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
    private Boolean financialBusiness;

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
     * 行业ID
     */
    private String industryId;

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
    private Boolean onlineSales;

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
    private EnterpriseAttributeReq attribute;


}
