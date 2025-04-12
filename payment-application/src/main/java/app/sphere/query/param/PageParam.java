package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageParam extends OperatorParam {

    /**
     * 分页参数
     */
    private Integer pageNum;

    /**
     * 分页参数
     */
    private Integer pageSize;
}
