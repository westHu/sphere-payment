package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.MerchantPayoutChannelConfig;
import infrastructure.sphere.db.mapper.MerchantPayoutChannelConfigMapper;
import domain.sphere.repository.MerchantPayoutChannelConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class MerchantPayoutChannelConfigRepositoryImpl
        extends ServiceImpl<MerchantPayoutChannelConfigMapper, MerchantPayoutChannelConfig>
        implements MerchantPayoutChannelConfigRepository {
}
