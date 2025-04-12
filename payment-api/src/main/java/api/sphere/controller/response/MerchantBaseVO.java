package api.sphere.controller.response;

import lombok.Data;

import java.util.List;

@Data
public class MerchantBaseVO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户性质：个人、企业、机构
     */
    private Integer merchantType;

    /**
     * 商户等级
     */
    private Integer merchantLevel;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 区域
     */
    private List<Integer> areaList;

    /**
     * 扩展
     */
    private String attribute;

}
