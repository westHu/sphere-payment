package com.paysphere.command.dto;

import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.db.entity.MerchantOperator;
import lombok.Data;

@Data
public class LoginDTO {

    /**
     * 商户基本信息
     */
    private Merchant merchant;

    /**
     * 基本配置
     */
    private MerchantConfig merchantConfig;

    /**
     * 操作员信息
     */
    private MerchantOperator merchantOperator;
}
