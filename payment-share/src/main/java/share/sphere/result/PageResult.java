package share.sphere.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import share.sphere.exception.ExceptionCode;

import java.util.List;

/**
 * 分页数据返回实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageResult<T> extends BaseResult {

    private Long total;

    private Long current;

    private List<T> data;

    public static <T> PageResult<T> ok(Long total, Long current, List<T> data) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setCode(ExceptionCode.SUCCESS.getCode());
        pageResult.setMessage(ExceptionCode.SUCCESS.getMessage());
        pageResult.setTotal(total);
        pageResult.setCurrent(current);
        pageResult.setData(data);
        return pageResult;
    }

}
