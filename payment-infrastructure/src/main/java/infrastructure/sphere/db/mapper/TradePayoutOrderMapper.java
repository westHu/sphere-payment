package infrastructure.sphere.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 代付订单Mapper
 * 处理代付订单的数据库操作，包括订单的增删改查等基础操作
 */
@Mapper
public interface TradePayoutOrderMapper extends BaseMapper<TradePayoutOrder> {

}
