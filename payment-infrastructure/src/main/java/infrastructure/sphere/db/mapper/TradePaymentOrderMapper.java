package infrastructure.sphere.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收款订单Mapper
 * 处理收款订单的数据库操作，包括订单的增删改查等基础操作
 */
@Mapper
public interface TradePaymentOrderMapper extends BaseMapper<TradePaymentOrder> {

}
