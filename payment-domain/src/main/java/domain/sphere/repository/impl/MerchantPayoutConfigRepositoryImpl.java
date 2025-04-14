package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.MerchantPayoutConfigRepository;
import infrastructure.sphere.db.entity.MerchantPayoutConfig;
import infrastructure.sphere.db.mapper.MerchantPayoutConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class MerchantPayoutConfigRepositoryImpl
        extends ServiceImpl<MerchantPayoutConfigMapper, MerchantPayoutConfig>
        implements MerchantPayoutConfigRepository {
}
