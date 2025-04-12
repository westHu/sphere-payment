package app.sphere.command.cmd;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantVerifyCommand extends OperatorCommand {

    /**
     * 商户ID
     */
    private String merchantId;

}
