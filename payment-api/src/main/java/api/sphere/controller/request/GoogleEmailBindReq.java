package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleEmailBindReq {

    /**
     * 登录名
     */
    @NotBlank(message = "username is required")
    private String username;

    /**
     * google ID-token
     */
    @NotBlank(message = "googleCode is required")
    private String googleCode;

}
