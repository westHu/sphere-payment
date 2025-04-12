package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.MerchantPayoutConfig;
import infrastructure.sphere.db.mapper.MerchantPayoutConfigMapper;
import domain.sphere.repository.MerchantPayoutConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class MerchantPayoutConfigRepositoryImpl
        extends ServiceImpl<MerchantPayoutConfigMapper, MerchantPayoutConfig>
        implements MerchantPayoutConfigRepository {
}
