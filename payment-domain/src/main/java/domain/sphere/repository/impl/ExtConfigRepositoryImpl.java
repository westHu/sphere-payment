package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.ExtConfig;
import infrastructure.sphere.db.mapper.ExtConfigMapper;
import domain.sphere.repository.ExtConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class ExtConfigRepositoryImpl extends ServiceImpl<ExtConfigMapper, ExtConfig>
        implements ExtConfigRepository {
}
