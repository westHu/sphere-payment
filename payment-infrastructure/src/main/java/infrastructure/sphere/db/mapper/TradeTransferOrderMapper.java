package infrastructure.sphere.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import infrastructure.sphere.db.entity.TradeTransferOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 转账订单Mapper
 * 处理转账订单的数据库操作，包括订单的增删改查等基础操作
 */
@Mapper
public interface TradeTransferOrderMapper extends BaseMapper<TradeTransferOrder> {

}
