package com.paysphere.controller.request;

import lombok.Data;

@Data
public class PersonAttributeReq {

    /**
     * 身份证正面 JPG
     */
    private String imgFrontOfIdCard;

    /**
     * 反面 JPG
     */
    private String imgBackOfIdCard;

    /**
     * logo JPG
     */
    private String imgLogo;

    /**
     * 存折
     */
    private String fileBankBook;

}
