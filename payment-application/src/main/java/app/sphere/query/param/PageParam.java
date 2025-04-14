package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class PageParam {

    /**
     * 分页参数
     */
    private Integer pageNum;

    /**
     * 分页参数
     */
    private Integer pageSize;
}
