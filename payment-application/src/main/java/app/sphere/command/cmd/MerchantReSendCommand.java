package app.sphere.command.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantReSendCommand {

    @NotBlank(message = "username is required")
    private String username;

}
