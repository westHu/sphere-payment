package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.MerchantWithdrawConfig;
import infrastructure.sphere.db.mapper.MerchantWithdrawConfigMapper;
import domain.sphere.repository.MerchantWithdrawConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class MerchantWithdrawConfigRepositoryImpl extends ServiceImpl<MerchantWithdrawConfigMapper, MerchantWithdrawConfig>
        implements MerchantWithdrawConfigRepository {
}
