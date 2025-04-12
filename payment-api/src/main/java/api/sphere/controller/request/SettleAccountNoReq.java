package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class SettleAccountNoReq {


    /**
     * 账户号
     */
    @NotBlank(message = "accountNo is required")
    private String accountNo;

}
