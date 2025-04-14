package api.sphere.convert;


import api.sphere.controller.request.*;
import api.sphere.controller.response.TradePaymentLinkOrderVO;
import app.sphere.command.cmd.*;
import app.sphere.query.param.*;
import infrastructure.sphere.db.entity.TradePaymentLinkOrder;
import org.mapstruct.*;
import share.sphere.enums.TradePaymentSourceEnum;

import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradePayConverter {

    @Mapping(source = "source", target = "tradePaySource", qualifiedByName = "toTradePaymentSourceEnum")
    @Mapping(source = "orderNo", target = "orderNo")
    @Mapping(source = "purpose", target = "purpose")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "money", target = "money")
    @Mapping(source = "merchant", target = "merchant")
    @Mapping(source = "payer", target = "payer")
    @Mapping(source = "receiver", target = "receiver")
    @Mapping(source = "expiryPeriod", target = "expiryPeriod")
    TradePaymentCmd convertTradePayCmd(TradePaymentReq req);

    TradePaymentOrderPageParam convertPageParam(TradePayOrderPageReq req);

    TradeCashierPaymentCmd convertTradeCashierPaymentCmd(TradeCashierPaymentReq req);

    CashierParam convertCashierParam(CashierReq req);

    TradePaymentRefundCmd convertTradePaymentRefundCmd(TradePaymentRefundReq req);

    TradePaymentSupplementCmd convertTradePaymentSupplementCmd(TradePaymentSupplementReq req);

    TradePaymentLinkPageParam convertTradePaymentLinkPageParam(TradePaymentLinkPageReq req);

    List<TradePaymentLinkOrderVO> convertTradePaymentLinkOrderVOList(List<TradePaymentLinkOrder> records);

    @Named("toTradePaymentSourceEnum")
    default TradePaymentSourceEnum toTradePaymentSourceEnum(Integer source) {
        if (source == null) {
            return TradePaymentSourceEnum.UNKNOWN;
        }
        return TradePaymentSourceEnum.codeToEnum(source);
    }
}
