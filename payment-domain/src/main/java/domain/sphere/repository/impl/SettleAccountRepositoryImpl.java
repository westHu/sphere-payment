package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.SettleAccount;
import infrastructure.sphere.db.mapper.SettleAccountMapper;
import domain.sphere.repository.SettleAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class SettleAccountRepositoryImpl extends ServiceImpl<SettleAccountMapper, SettleAccount>
        implements SettleAccountRepository {
}
