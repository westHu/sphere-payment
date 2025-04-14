package app.sphere.command.cmd;

import lombok.Data;

import java.util.List;

@Data
public class MerchantWithdrawJobCommand {

    /**
     * 商户ID列表
     */
    private List<String> merchantIdList;
}
