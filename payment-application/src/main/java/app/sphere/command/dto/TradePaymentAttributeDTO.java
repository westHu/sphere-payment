package app.sphere.command.dto;

import lombok.Data;

@Data
public class TradePaymentAttributeDTO {

    /**
     * 有效期
     */
    private Integer expiryPeriod;

    /**
     * 店铺名称
     */
    private String shopName;

}
