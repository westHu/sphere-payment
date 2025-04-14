package domain.sphere.repository.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.PaymentChannelBalanceLog;
import infrastructure.sphere.db.mapper.PaymentChannelBalanceLogMapper;
import domain.sphere.repository.PaymentChannelBalanceLogRepository;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class PaymentChannelBalanceLogRepositoryImpl extends ServiceImpl<PaymentChannelBalanceLogMapper,
        PaymentChannelBalanceLog> implements PaymentChannelBalanceLogRepository {

}
