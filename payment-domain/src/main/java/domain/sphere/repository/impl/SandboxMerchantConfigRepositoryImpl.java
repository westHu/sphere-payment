package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SandboxMerchantConfigRepository;
import infrastructure.sphere.db.entity.SandboxMerchantConfig;
import infrastructure.sphere.db.mapper.SandboxMerchantConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class SandboxMerchantConfigRepositoryImpl extends ServiceImpl<SandboxMerchantConfigMapper, SandboxMerchantConfig>
        implements SandboxMerchantConfigRepository {
}
