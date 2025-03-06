package com.paysphere.command.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccountApplyNobuDTO {

    /**
     * merchant
     */
    private String phone;
    private String email;
    private String merchantType;
    private String merchantName;
    private String merchantDescription;

    /**
     * business
     */
    private String businessCriteria;
    private String businessCategory;
    private String businessEntity;

    /**
     * address
     */
    private String city;
    private String province;
    private String district;
    private String urban;
    private String address;
    private String zipCode;

    /**
     * ktp
     */
    private String name;
    private String brandName;
    private String ktpNo;
    private String ktpPhoto;
    private List<String> nibPhoto;
    private List<String> npwpPhoto;

}
