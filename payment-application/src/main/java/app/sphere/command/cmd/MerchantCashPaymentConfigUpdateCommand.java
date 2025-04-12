package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantCashPaymentConfigUpdateCommand {

    private String merchantId;

    private BigDecimal singleRate;

    private BigDecimal singleFee;

}
