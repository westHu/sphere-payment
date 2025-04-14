package api.sphere.controller.request;

import api.sphere.config.EnumValid;
import lombok.Data;
import share.sphere.enums.QuerySourceEnum;

@Data
public class QuerySourceReq {

    /**
     * 来源
     */
    @EnumValid(target = QuerySourceEnum.class, transferMethod = "getCode", message = "querySource not support")
    private Integer querySource;

}
