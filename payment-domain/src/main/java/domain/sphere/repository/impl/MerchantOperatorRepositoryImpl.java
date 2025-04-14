package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.MerchantOperator;
import infrastructure.sphere.db.mapper.MerchantOperatorMapper;
import domain.sphere.repository.MerchantOperatorRepository;
import org.springframework.stereotype.Service;

@Service
public class MerchantOperatorRepositoryImpl extends ServiceImpl<MerchantOperatorMapper, MerchantOperator>
        implements MerchantOperatorRepository {
}
