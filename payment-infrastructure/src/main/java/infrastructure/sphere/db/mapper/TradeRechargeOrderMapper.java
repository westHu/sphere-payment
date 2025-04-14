package infrastructure.sphere.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import infrastructure.sphere.db.entity.TradeRechargeOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 充值订单Mapper
 * 处理充值订单的数据库操作，包括订单的增删改查等基础操作
 */
@Mapper
public interface TradeRechargeOrderMapper extends BaseMapper<TradeRechargeOrder> {

}
