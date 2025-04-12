package app.sphere.command.cmd;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户更新
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantUpdateStatusCommand extends MerchantIdCommand{

    /**
     * 商户状态
     */
    private Integer status;
}
