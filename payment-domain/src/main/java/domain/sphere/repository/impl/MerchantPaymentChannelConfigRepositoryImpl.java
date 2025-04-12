package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.MerchantPaymentChannelConfig;
import infrastructure.sphere.db.mapper.MerchantPaymentChannelConfigMapper;
import domain.sphere.repository.MerchantPaymentChannelConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class MerchantPaymentChannelConfigRepositoryImpl
        extends ServiceImpl<MerchantPaymentChannelConfigMapper, MerchantPaymentChannelConfig>
        implements MerchantPaymentChannelConfigRepository {
}
