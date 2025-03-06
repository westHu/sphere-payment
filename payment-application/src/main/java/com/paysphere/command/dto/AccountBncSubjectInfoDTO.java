package com.paysphere.command.dto;

import lombok.Data;

@Data
public class AccountBncSubjectInfoDTO {

    /**
     * 省
     */
    private String provinceId;

    /**
     * 省
     */
    private String provinceName;

    /**
     * 市区
     */
    private String cityId;

    /**
     * 市区
     */
    private String cityIdShort;

    /**
     * 市区
     */
    private String cityName;

    /**
     * 市区
     */
    private String districtId;

    /**
     * 市区
     */
    private String districtName;

    /**
     * 地址
     */
    private String merchantAddress;

    /**
     * 右边
     */
    private String postcodeId;

    /**
     *
     */
    private String merchantExternalMid;

    /**
     *
     */
    private String parentMerchantId;

    /**
     *
     */
    private String ktpCopy;

    /**
     *
     */
    private String ktpNumber;

    /**
     *
     */
    private String licenseCopy;

    /**
     *
     */
    private String licenseNumber;

    /**
     *
     */
    private String merchantName;

    /**
     *
     */
    private String merchantType;

    /**
     *
     */
    private String nibCopy;

    /**
     *
     */
    private String nibNumber;

    /**
     *
     */
    private String npwpCopy;

    /**
     *
     */
    private String npwpNumber;

}
