package com.paysphere.command.dto;

import lombok.Data;

@Data
public class GoogleUserInfoDTO {

    /**
     * email
     */
    private String email;

    /**
     * name
     */
    private String name;

    /**
     * pictureUrl
     */
    private String pictureUrl;
}
