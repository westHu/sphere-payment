package api.sphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageReq {

    /**
     * 分页参数
     */
    @NotNull
    private Integer pageNum = 1;
    /**
     * 分页参数
     */
    @NotNull
    private Integer pageSize = 20;

}
