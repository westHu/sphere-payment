package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class PersonAttributeCommand {

    /**
     * 身份证正面 JPG
     */
    //@NotBlank(message = "imgFrontOfIdCard is required")
    private String imgFrontOfIdCard;

    /**
     * 反面 JPG
     */
    //@NotBlank(message = "imgBackOfIdCard is required")
    private String imgBackOfIdCard;

    /**
     * logo JPG
     */
    //@NotBlank(message = "imgLogo is required")
    private String imgLogo;

    /**
     * 存折
     */
    private String fileBankBook;

}
