package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.MerchantPaymentConfigRepository;
import infrastructure.sphere.db.entity.MerchantPaymentConfig;
import infrastructure.sphere.db.mapper.MerchantPaymentConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class MerchantPaymentConfigRepositoryImpl extends ServiceImpl<MerchantPaymentConfigMapper, MerchantPaymentConfig>
        implements MerchantPaymentConfigRepository {
}
