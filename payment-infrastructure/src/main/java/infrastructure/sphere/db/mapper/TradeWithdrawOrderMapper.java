package infrastructure.sphere.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现订单Mapper
 * 处理提现订单的数据库操作，包括订单的增删改查等基础操作
 */
@Mapper
public interface TradeWithdrawOrderMapper extends BaseMapper<TradeWithdrawOrder> {

}
