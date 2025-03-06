package com.paysphere.convert;

import com.paysphere.command.cmd.PaymentFinishMessageCommand;
import com.paysphere.command.cmd.SettleAccountFlowRevisionJobCommand;
import com.paysphere.command.cmd.SettleAccountSnapshotJobCommand;
import com.paysphere.command.cmd.SettleFinishMessageCommand;
import com.paysphere.command.cmd.SettleJobCommand;
import com.paysphere.command.cmd.TradeFileJobCommand;
import com.paysphere.command.cmd.TradePayOrderTimeOutJobCommand;
import com.paysphere.command.cmd.TradeStatisticsSnapshotJobCommand;
import com.paysphere.job.param.AccountFlowRevisionJobParam;
import com.paysphere.job.param.AccountSnapshotJobParam;
import com.paysphere.job.param.SettleJobParam;
import com.paysphere.job.param.TradeFileJobParam;
import com.paysphere.job.param.TradePayOrderTimeOutJobParam;
import com.paysphere.job.param.TradeStatisticsSnapshotJobParam;
import com.paysphere.message.dto.PaymentFinishMessageDTO;
import com.paysphere.message.dto.SettleFinishMessageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface JobConverter {

    TradePayOrderTimeOutJobCommand convertTradePayOrderTimeOutJobCommand(TradePayOrderTimeOutJobParam param);

    TradeStatisticsSnapshotJobCommand convertTradeStatisticsSnapshotJobCommand(TradeStatisticsSnapshotJobParam param);

    TradeFileJobCommand convertMerchantFileJobCommand(TradeFileJobParam param);

    PaymentFinishMessageCommand convertPaymentFinishMessageCommand(PaymentFinishMessageDTO dto);

    SettleFinishMessageCommand convertSettleFinishMessageCommand(SettleFinishMessageDTO dto);

    SettleAccountSnapshotJobCommand convertAccountSnapshotJobCommand(AccountSnapshotJobParam param);

    SettleAccountFlowRevisionJobCommand convertAccountFlowRevisionJobCommand(AccountFlowRevisionJobParam param);

    SettleJobCommand convertSettleJobCommand(SettleJobParam param);
}
