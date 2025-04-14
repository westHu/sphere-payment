package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.PaymentMethodRepository;
import infrastructure.sphere.db.entity.PaymentMethod;
import infrastructure.sphere.db.mapper.PaymentMethodMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodRepositoryImpl extends ServiceImpl<PaymentMethodMapper, PaymentMethod>
        implements PaymentMethodRepository {
}
