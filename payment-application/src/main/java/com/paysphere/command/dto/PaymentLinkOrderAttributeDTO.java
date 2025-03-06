package com.paysphere.command.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaymentLinkOrderAttributeDTO {

    /**
     * 通知郵件
     */
    private List<String> notificationEmail;

}
