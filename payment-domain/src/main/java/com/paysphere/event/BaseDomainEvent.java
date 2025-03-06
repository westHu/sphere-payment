package com.paysphere.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 领域事件基类
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseDomainEvent<T> {

    private static final long serialVersionUID = 1L;
    /**
     * 事件时间
     */
    private LocalDateTime eventTime;
    /**
     * 领域事件数据
     */
    private T data;

}