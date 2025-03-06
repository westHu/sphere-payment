package com.paysphere.query.dto;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class PageDTO<T> {

    /**
     * 总数量
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 数据记录
     */
    private List<T> data;

    public static <T> PageDTO<T> empty() {
        PageDTO<T> pageDTO = new PageDTO<>();
        pageDTO.setTotal(0L);
        pageDTO.setCurrent(0L);
        pageDTO.setData(Collections.emptyList());
        return pageDTO;
    }


    public static <T> PageDTO<T> of(Long total, Long current, List<T> data) {
        PageDTO<T> pageDTO = new PageDTO<>();
        pageDTO.setTotal(total);
        pageDTO.setCurrent(current);
        pageDTO.setData(data);
        return pageDTO;
    }
}
