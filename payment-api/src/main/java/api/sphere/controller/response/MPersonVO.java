package api.sphere.controller.response;

import lombok.Data;

@Data
public class MPersonVO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 全名
     */
    private String fullName;

    /**
     * 证件号码
     */
    private String certificateNumber;

    /**
     * 出生地
     */
    private String birthAddress;

    /**
     * 出生日期
     */
    private String birthDate;

    /**
     * 地址
     */
    private String address;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 行业
     */
    private String industry;

    /**
     * 公司产品/服务
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
     * 税号
     */
    private String taxNumber;

    /**
     * 扩展信息
     */
    private String attribute;

}
