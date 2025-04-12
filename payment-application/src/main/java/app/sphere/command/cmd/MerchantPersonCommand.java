package app.sphere.command.cmd;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 个人商户
 */
@Data
public class MerchantPersonCommand {

    /**
     * 0011
     * 商业需要 收款1、出款2 收款出款3
     */
    private Integer businessAction;

    /**
     * 全名
     */
    @NotBlank(message = "fullName is required")
    private String fullName;

    /**
     * 证件号码
     */
    @NotBlank(message = "certificateNumber is required")
    private String certificateNumber;

    /**
     * 出生地
     */
    @NotBlank(message = "birthAddress is required")
    private String birthAddress;

    /**
     * 出生日期
     */
    @NotBlank(message = "birthDate is required")
    private String birthDate;

    /**
     * 地址
     */
    @NotBlank(message = "address is required")
    private String address;

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
     * 公司产品/服务
     */
    @NotBlank(message = "product is required")
    private String product;

    /**
     * 是否网上销售商品/服务
     */
    @NotNull(message = "onlineSales is required")
    private boolean onlineSales;

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
     * 税号
     */
    private String taxNumber;

    /**
     * 负责人手机
     */
    private String picPhone;

    /**
     * 扩展信息
     */
    @NotNull(message = "attribute img is required")
    @Valid
    private PersonAttributeCommand attribute;


}
