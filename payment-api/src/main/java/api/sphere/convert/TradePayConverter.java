package api.sphere.convert;


import app.sphere.command.cmd.TradeCashierPaymentCmd;
import app.sphere.command.cmd.TradePaymentCmd;
import app.sphere.command.cmd.TradePaymentRefundCmd;
import app.sphere.command.cmd.TradePaymentSupplementCmd;
import api.sphere.controller.request.CashierReq;
import api.sphere.controller.request.TradeCashierPaymentReq;
import api.sphere.controller.request.TradePayOrderPageReq;
import api.sphere.controller.request.TradePaymentLinkPageReq;
import api.sphere.controller.request.TradePaymentRefundReq;
import api.sphere.controller.request.TradePaymentReq;
import api.sphere.controller.request.TradePaymentSupplementReq;
import api.sphere.controller.response.TradePaymentLinkOrderVO;
import infrastructure.sphere.db.entity.TradePaymentLinkOrder;
import share.sphere.enums.TradePaymentSourceEnum;
import app.sphere.query.param.CashierParam;
import app.sphere.query.param.TradePayOrderPageParam;
import app.sphere.query.param.TradePaymentLinkPageParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

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

    TradePayOrderPageParam convertPageParam(TradePayOrderPageReq req);

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
