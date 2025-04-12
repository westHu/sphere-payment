package api.sphere.convert;

import api.sphere.job.param.AccountFlowRevisionJobParam;
import api.sphere.job.param.AccountSnapshotJobParam;
import api.sphere.job.param.SettleJobParam;
import api.sphere.job.param.TradeFileJobParam;
import api.sphere.job.param.TradePayOrderTimeOutJobParam;
import api.sphere.job.param.TradeStatisticsSnapshotJobParam;
import app.sphere.command.cmd.SettleAccountFlowRevisionJobCommand;
import app.sphere.command.cmd.SettleAccountSnapshotJobCommand;
import app.sphere.command.cmd.SettleJobCommand;
import app.sphere.command.cmd.TradeFileJobCommand;
import app.sphere.command.cmd.TradePayOrderTimeOutJobCommand;
import app.sphere.command.cmd.TradeStatisticsSnapshotJobCommand;
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
