package domain.sphere.event;

import lombok.*;

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