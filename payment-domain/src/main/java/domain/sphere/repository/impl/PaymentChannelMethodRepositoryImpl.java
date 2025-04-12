package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
import infrastructure.sphere.db.mapper.PaymentChannelMethodMapper;
import domain.sphere.repository.PaymentChannelMethodRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentChannelMethodRepositoryImpl extends ServiceImpl<PaymentChannelMethodMapper, PaymentChannelMethod>
        implements PaymentChannelMethodRepository {
}
