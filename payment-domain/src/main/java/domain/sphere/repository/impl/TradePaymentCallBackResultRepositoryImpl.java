package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.TradePaymentCallBackResultRepository;
import infrastructure.sphere.db.entity.TradePaymentCallBackResult;
import infrastructure.sphere.db.mapper.TradePaymentCallBackResultMapper;
import org.springframework.stereotype.Service;

@Service
public class TradePaymentCallBackResultRepositoryImpl extends ServiceImpl<TradePaymentCallBackResultMapper, TradePaymentCallBackResult>
        implements TradePaymentCallBackResultRepository {
}
