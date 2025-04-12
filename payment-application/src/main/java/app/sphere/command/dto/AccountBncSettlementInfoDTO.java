package app.sphere.command.dto;

import lombok.Data;

@Data
public class AccountBncSettlementInfoDTO {

    /**
     *
     */
    private String criteria = "01";

    /**
     * 商品识别码
     */
    private String mcc;

    /**
     *
     */
    private String qualifications;

    /**
     *
     */
    private String terminalNumber = "1";

}
