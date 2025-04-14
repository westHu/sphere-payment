package app.sphere.assembler;


import app.sphere.command.cmd.SettlePaymentCommand;
import app.sphere.command.cmd.SettlePayoutCommand;
import app.sphere.command.cmd.SettleSupplementCmd;
import app.sphere.command.cmd.TradePayoutReviewCommand;
import app.sphere.command.cmd.TradeRechargeReviewCommand;
import app.sphere.command.cmd.TradeReviewCommand;
import app.sphere.command.cmd.TradeTransferReviewCommand;
import app.sphere.command.cmd.TradeWithdrawReviewCommand;
import app.sphere.query.dto.MerchantConfigDTO;
import app.sphere.query.dto.MerchantPayoutConfigDTO;
import infrastructure.sphere.db.entity.MerchantConfig;
import infrastructure.sphere.db.entity.MerchantPayoutConfig;
import infrastructure.sphere.db.entity.SettleAccount;
import infrastructure.sphere.db.entity.SettleAccountSnapshot;
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