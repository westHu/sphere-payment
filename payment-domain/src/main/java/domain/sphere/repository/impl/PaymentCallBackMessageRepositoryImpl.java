package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.PaymentCallBackMessage;
import infrastructure.sphere.db.mapper.PaymentCallBackMessageMapper;
import domain.sphere.repository.PaymentCallBackMessageRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentCallBackMessageRepositoryImpl extends ServiceImpl<PaymentCallBackMessageMapper, PaymentCallBackMessage>
        implements PaymentCallBackMessageRepository {
}
