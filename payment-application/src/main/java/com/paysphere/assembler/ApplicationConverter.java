package com.paysphere.assembler;


import com.paysphere.command.cmd.SettleCashMessageCommand;
import com.paysphere.command.cmd.SettlePayMessageCommand;
import com.paysphere.command.cmd.SettleSupplementCmd;
import com.paysphere.command.cmd.TradeCashReviewCommand;
import com.paysphere.command.cmd.TradeRechargeReviewCommand;
import com.paysphere.command.cmd.TradeReviewCommand;
import com.paysphere.command.cmd.TradeTransferReviewCommand;
import com.paysphere.command.cmd.TradeWithdrawReviewCommand;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.db.entity.MerchantPayoutConfig;
import com.paysphere.db.entity.SettleAccount;
import com.paysphere.db.entity.SettleAccountSnapshot;
import com.paysphere.query.dto.MerchantConfigDTO;
import com.paysphere.query.dto.MerchantPayoutConfigDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface ApplicationConverter {


    TradeCashReviewCommand convertTradeCashReviewCommand(TradeReviewCommand e);

    TradeTransferReviewCommand convertTradeTransferReviewCommand(TradeReviewCommand command);

    TradeRechargeReviewCommand convertTradeRechargeReviewCommand(TradeReviewCommand command);

    TradeWithdrawReviewCommand convertTradeWithdrawReviewCommand(TradeReviewCommand command);

    MerchantPayoutConfigDTO convertMerchantPayoutConfigDTO(MerchantPayoutConfig cashConfig);

    MerchantConfigDTO convertMerchantConfigDTO(MerchantConfig merchantConfig);

    SettlePayMessageCommand convertSettlePayMessageCommand(SettleSupplementCmd command);

    SettleCashMessageCommand convertSettleCashMessageCommand(SettleSupplementCmd command);

    SettleAccountSnapshot convertAccountDailySnapshot(SettleAccount e);
}