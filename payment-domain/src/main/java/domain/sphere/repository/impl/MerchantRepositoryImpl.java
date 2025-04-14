package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.MerchantRepository;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.mapper.MerchantMapper;
import org.springframework.stereotype.Service;

@Service
public class MerchantRepositoryImpl extends ServiceImpl<MerchantMapper, Merchant>
        implements MerchantRepository {
}
