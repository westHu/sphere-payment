package app.sphere.command.impl;

import app.sphere.command.TradeFileJobCmdService;
import app.sphere.command.cmd.TradeFileJobCommand;
import cn.hutool.json.JSONUtil;
import domain.sphere.repository.TradePaymentOrderRepository;
import domain.sphere.repository.TradePayoutOrderRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TradeFileJobCmdServiceImpl implements TradeFileJobCmdService {

    @Resource
    TradePaymentOrderRepository tradePaymentOrderRepository;
    @Resource
    TradePayoutOrderRepository tradePayoutOrderRepository;

    @Override
    public void handlerTradeFile(TradeFileJobCommand command) {
        log.info("trade csv file command={}", JSONUtil.toJsonStr(command));
    }

}
