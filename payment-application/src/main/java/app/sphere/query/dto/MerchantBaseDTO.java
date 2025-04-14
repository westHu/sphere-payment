package app.sphere.query.dto;

import lombok.Data;
import share.sphere.enums.MerchantStatusEnum;

@Data
public class MerchantBaseDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户性质： 个人、企业、机构
     */
    private Integer merchantType;

    /**
     * 等级
     */
    private Integer merchantLevel;

    /**
     * 状态
     *
     * @see MerchantStatusEnum
     */
    private Integer status;

}
