package app.sphere.command.cmd;

import lombok.Data;

@Data
public class SettleAccountSnapshotJobCommand {

    /**
     * 商户ID
     */
    private String merchantId;
}
