package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.PaymentCallBackMessageRepository;
import infrastructure.sphere.db.entity.PaymentCallBackMessage;
import infrastructure.sphere.db.mapper.PaymentCallBackMessageMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentCallBackMessageRepositoryImpl extends ServiceImpl<PaymentCallBackMessageMapper, PaymentCallBackMessage>
        implements PaymentCallBackMessageRepository {
}
