package com.paysphere.mq.dto.email;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum EmailContentTypeEnum {


    TEXT("文本"),
    HTML("HTML"),
    ATTACHMENT("附件"),
    TEMPLATE("模板");

    private final String name;


}
