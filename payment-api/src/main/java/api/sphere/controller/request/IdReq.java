package api.sphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IdReq {

    /**
     * ID
     */
    @NotNull(message = "id is required")
    private Long id;
}
