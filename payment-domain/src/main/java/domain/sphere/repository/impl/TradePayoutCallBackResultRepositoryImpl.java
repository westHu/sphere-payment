package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.TradePayoutCallBackResultRepository;
import infrastructure.sphere.db.entity.TradePayoutCallBackResult;
import infrastructure.sphere.db.mapper.TradePayoutCallBackResultMapper;
import org.springframework.stereotype.Service;

@Service
public class TradePayoutCallBackResultRepositoryImpl extends ServiceImpl<TradePayoutCallBackResultMapper,
        TradePayoutCallBackResult>
        implements TradePayoutCallBackResultRepository {
}
