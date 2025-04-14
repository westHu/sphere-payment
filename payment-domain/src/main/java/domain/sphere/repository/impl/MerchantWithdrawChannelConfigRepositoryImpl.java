package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.MerchantWithdrawChannelConfigRepository;
import infrastructure.sphere.db.entity.MerchantWithdrawChannelConfig;
import infrastructure.sphere.db.mapper.MerchantWithdrawPaymentConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class MerchantWithdrawChannelConfigRepositoryImpl extends ServiceImpl<MerchantWithdrawPaymentConfigMapper,
        MerchantWithdrawChannelConfig>
        implements MerchantWithdrawChannelConfigRepository {
}
