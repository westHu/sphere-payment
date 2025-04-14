package api.sphere.convert;


import api.sphere.controller.request.*;
import app.sphere.query.param.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface SnapshotTradeStatisticsConverter {

    TradeMerchantStatisticsSnapshotParam convertTradeMerchantStatisticsSnapshotParam(TradeMerchantStatisticsSnapshotReq req);

    TradeTimelyStatisticsIndexParam convertTradeTimelyStatisticsIndexParam(TradeTimelyStatisticsIndexReq req);

    TradeStatisticsPlatformParam convertTradeStatisticsPlatformParam(TradeStatisticsPlatformReq req);

    TradeStatisticsChannelParam convertTradeStatisticsChannelParam(TradeStatisticsChannelReq req);

    TradeStatisticsMerchantParam convertTradeStatisticsMerchantParam(TradeStatisticsMerchantReq req);

}
