package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.PaymentChannel;
import infrastructure.sphere.db.mapper.PaymentChannelMapper;
import domain.sphere.repository.PaymentChannelRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentChannelRepositoryImpl extends ServiceImpl<PaymentChannelMapper, PaymentChannel>
        implements PaymentChannelRepository {
}
