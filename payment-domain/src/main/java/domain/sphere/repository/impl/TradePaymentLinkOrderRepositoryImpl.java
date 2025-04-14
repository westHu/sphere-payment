package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.TradePaymentLinkOrder;
import infrastructure.sphere.db.mapper.TradePaymentLinkOrderMapper;
import domain.sphere.repository.TradePaymentLinkOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class TradePaymentLinkOrderRepositoryImpl extends ServiceImpl<TradePaymentLinkOrderMapper, TradePaymentLinkOrder>
        implements TradePaymentLinkOrderRepository {
}
