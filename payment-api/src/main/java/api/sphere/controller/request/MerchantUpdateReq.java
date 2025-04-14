package api.sphere.controller.request;


import api.sphere.config.EnumValid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import share.sphere.enums.MerchantStatusEnum;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantUpdateReq extends MerchantIdReq {

    /**
     * 品牌名称
     * 商户的品牌名称，用于展示
     */
    private String brandName;

    /**
     * 商户性质
     * 1: 个人
     * 2: 企业
     * 3: 机构
     */
    private Integer merchantType;

    /**
     * API对接模式
     * 1: API模式
     * 2: 收银台模式
     * 3: API+收银台模式
     */
    private Integer apiMode;

    /**
     * 支持的币种
     * 商户支持的交易币种列表，如：CNY, USD, EUR等
     */
    private List<String> currencyList;

    /**
     * 商户标签
     */
    private List<String> tags;

    /**
     * 商户状态
     */
    @NotNull(message = "status is required")
    @EnumValid(target = MerchantStatusEnum.class, transferMethod = "getCode", message = "status type not support")
    private Integer status;

}
