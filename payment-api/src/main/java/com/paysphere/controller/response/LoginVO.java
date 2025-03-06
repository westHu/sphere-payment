package com.paysphere.controller.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginVO {

    /**
     * 商户基本信息
     */
    private MerchantBaseVO merchant;

    /**
     * 商户基本配置 1对1
     */
    private MerchantConfigVO merchantConfig;

    /**
     * 商户操作员 1对多
     */
    private List<MerchantOperatorVO> merchantOperatorList;

}
