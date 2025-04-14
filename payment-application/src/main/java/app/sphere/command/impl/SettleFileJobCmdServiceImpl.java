package app.sphere.command.impl;

import app.sphere.command.SettleFileJobCmdService;
import app.sphere.command.cmd.SettleFileJobCommand;
import cn.hutool.json.JSONUtil;
import domain.sphere.repository.SettleOrderRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


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
