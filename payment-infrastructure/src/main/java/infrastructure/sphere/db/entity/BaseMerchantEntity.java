package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商户基础实体类
 * 所有数据库实体类的父类，提供基础字段
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseMerchantEntity extends BaseEntity {


    // ============== 基础信息 ==============
    /**
     * 商户ID
     * 商户的唯一标识符
     */
    private String merchantId;

    /**
     * 商户名称
     * 商户的正式名称
     */
    private String merchantName;
}
