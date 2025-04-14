package app.sphere.command.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantWithdrawToDTO {

    /**
     * 结算\打款到哪
     */
    private String withdrawTo;

    /**
     * 比例
     */
    private BigDecimal ratio;

}
