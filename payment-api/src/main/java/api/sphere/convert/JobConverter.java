package api.sphere.convert;

import api.sphere.job.param.*;
import app.sphere.command.cmd.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface JobConverter {

    TradePayOrderTimeOutJobCommand convertTradePayOrderTimeOutJobCommand(TradePayOrderTimeOutJobParam param);

    TradeStatisticsSnapshotJobCommand convertTradeStatisticsSnapshotJobCommand(TradeStatisticsSnapshotJobParam param);

    TradeFileJobCommand convertMerchantFileJobCommand(TradeFileJobParam param);

    SettleAccountSnapshotJobCommand convertAccountSnapshotJobCommand(AccountSnapshotJobParam param);

    SettleAccountFlowRevisionJobCommand convertAccountFlowRevisionJobCommand(AccountFlowRevisionJobParam param);

    SettleJobCommand convertSettleJobCommand(SettleJobParam param);
}
