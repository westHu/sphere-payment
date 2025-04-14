package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.MerchantConfigRepository;
import infrastructure.sphere.db.entity.MerchantConfig;
import infrastructure.sphere.db.mapper.MerchantConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class MerchantConfigRepositoryImpl extends ServiceImpl<MerchantConfigMapper, MerchantConfig>
        implements MerchantConfigRepository {
}
