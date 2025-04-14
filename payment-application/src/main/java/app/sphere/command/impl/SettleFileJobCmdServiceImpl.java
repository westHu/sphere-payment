package app.sphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import app.sphere.command.SettleFileJobCmdService;
import app.sphere.command.cmd.SettleFileJobCommand;
import app.sphere.command.dto.SettleFileDTO;
import infrastructure.sphere.db.entity.BaseEntity;
import infrastructure.sphere.db.entity.SettleOrder;
import share.sphere.enums.SettleStatusEnum;
import domain.sphere.repository.SettleOrderRepository;
import share.sphere.utils.StorageUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class SettleFileJobCmdServiceImpl implements SettleFileJobCmdService {

    @Resource
    SettleOrderRepository settleOrderRepository;

    @Override
    public void handlerSettleFile(SettleFileJobCommand command) {
        log.info("handlerSettleFile command={}", JSONUtil.toJsonStr(command));
    }
}
