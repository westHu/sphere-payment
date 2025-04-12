package api.sphere.controller.response;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SettleAccountBalanceVO {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 扩展
     */
    private String attribute;

    /**
     * 余额信息
     */
    private List<SettleAccountBalanceDetailVO> accountList;
}
