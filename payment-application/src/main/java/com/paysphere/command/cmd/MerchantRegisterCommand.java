package com.paysphere.command.cmd;

import com.paysphere.enums.AreaEnum;
import lombok.Data;

/**
 * 商户注册
 */
@Data
public class MerchantRegisterCommand {

    /**
     * 商户名称
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邀请码
     */
    private String invitationCode;

    /**
     * 商户等级
     */
    private Integer merchantLevel;

    /**
     * 地区
     *
     * @see AreaEnum
     */
    private Integer area;

    /**
     * 谷歌授权信息
     */
    private String googleEmail;

}
