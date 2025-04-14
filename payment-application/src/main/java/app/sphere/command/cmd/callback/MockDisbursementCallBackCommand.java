package app.sphere.command.cmd.callback;

import lombok.Data;

@Data
public class MockDisbursementCallBackCommand {

    private String txId;

    private String tradeNo;

    private Integer status;

    private String message;
}
