package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.MerchantWithdrawConfigRepository;
import infrastructure.sphere.db.entity.MerchantWithdrawConfig;
import infrastructure.sphere.db.mapper.MerchantWithdrawConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class MerchantWithdrawConfigRepositoryImpl extends ServiceImpl<MerchantWithdrawConfigMapper, MerchantWithdrawConfig>
        implements MerchantWithdrawConfigRepository {
}
