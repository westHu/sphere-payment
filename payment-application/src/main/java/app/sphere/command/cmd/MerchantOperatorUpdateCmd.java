package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantOperatorUpdateCmd {

    /**
     * ID
     */
    private Long id;

    /**
     * 密码
     */
    private String password;
}
