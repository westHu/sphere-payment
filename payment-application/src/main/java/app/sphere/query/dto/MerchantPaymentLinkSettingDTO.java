package app.sphere.query.dto;

import app.sphere.command.dto.PaymentMethodSortedDTO;
import lombok.Data;

import java.util.List;

@Data
public class MerchantPaymentLinkSettingDTO {

    /**
     * logo
     */
    private String logo;

    /**
     * bgColor
     */
    private String bgColor;

    /**
     * 推荐支付方式
     */
    private List<String> recommendedPaymentMethod;

    /**
     * 排序支付方式
     */
    private List<PaymentMethodSortedDTO> sortedPaymentMethodList;

    /**
     * 水印
     */
    private String waterMark;

    /**
     * qr中是否放logo
     */
    private boolean qrLogo;

}
