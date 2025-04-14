package share.sphere.utils.dto;

import lombok.Data;

@Data
public class JWTTokenMerchantDTO {

    // ============== 基础信息 ==============
    /**
     * 商户ID
     * 商户的唯一标识符
     */
    private String merchantId;

    /**
     * 商户名称
     * 商户的正式名称
     */
    private String merchantName;

    /**
     * 品牌名称
     * 商户的品牌名称，用于展示
     */
    private String brandName;

}
