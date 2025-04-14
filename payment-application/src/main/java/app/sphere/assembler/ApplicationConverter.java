package app.sphere.assembler;


import app.sphere.command.cmd.*;
import app.sphere.query.dto.MerchantConfigDTO;
import app.sphere.query.dto.MerchantPayoutConfigDTO;
import infrastructure.sphere.db.entity.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface ApplicationConverter {

    TradePayoutReviewCommand convertTradeCashReviewCommand(TradeReviewCommand e);

    TradeTransferReviewCommand convertTradeTransferReviewCommand(TradeReviewCommand command);

    TradeRechargeReviewCommand convertTradeRechargeReviewCommand(TradeReviewCommand command);

    TradeWithdrawReviewCommand convertTradeWithdrawReviewCommand(TradeReviewCommand command);

    MerchantPayoutConfigDTO convertMerchantPayoutConfigDTO(MerchantPayoutConfig cashConfig);

    MerchantConfigDTO convertMerchantConfigDTO(MerchantConfig merchantConfig);

    SettlePaymentCommand convertSettlePayMessageCommand(SettleSupplementCmd command);

    SettlePayoutCommand convertSettlePayoutMessageCommand(SettleSupplementCmd command);

    SettleAccountSnapshot convertAccountDailySnapshot(SettleAccount account);
}