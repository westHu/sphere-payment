package com.paysphere.command.dto;

import lombok.Data;

import java.util.List;

@Data
public class NotifyContentDTO {

    /**
     * 图标
     */
    private String icon;

    /**
     * 关键字
     */
    private List<String> keywords;

    /**
     * 链接
     */
    private String link;

    /**
     * 图片
     */
    private String img;

    /**
     * 内容
     */
    private String content;

}
