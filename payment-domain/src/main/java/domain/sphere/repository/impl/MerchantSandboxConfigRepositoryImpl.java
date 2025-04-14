package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.MerchantSandboxConfigRepository;
import infrastructure.sphere.db.entity.MerchantSandboxConfig;
import infrastructure.sphere.db.mapper.MerchantSandboxConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class MerchantSandboxConfigRepositoryImpl extends ServiceImpl<MerchantSandboxConfigMapper, MerchantSandboxConfig>
        implements MerchantSandboxConfigRepository {
}
