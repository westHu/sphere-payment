package api.sphere.convert;


import api.sphere.controller.request.TradeMerchantStatisticsSnapshotReq;
import api.sphere.controller.request.TradeStatisticsChannelReq;
import api.sphere.controller.request.TradeStatisticsMerchantReq;
import api.sphere.controller.request.TradeStatisticsPlatformReq;
import api.sphere.controller.request.TradeTimelyStatisticsIndexReq;
import app.sphere.query.param.TradeMerchantStatisticsSnapshotParam;
import app.sphere.query.param.TradeStatisticsChannelParam;
import app.sphere.query.param.TradeStatisticsMerchantParam;
import app.sphere.query.param.TradeStatisticsPlatformParam;
import app.sphere.query.param.TradeTimelyStatisticsIndexParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface SnapshotTradeStatisticsConverter {

    TradeMerchantStatisticsSnapshotParam convertTradeMerchantStatisticsSnapshotParam(TradeMerchantStatisticsSnapshotReq req);

    TradeTimelyStatisticsIndexParam convertTradeTimelyStatisticsIndexParam(TradeTimelyStatisticsIndexReq req);

    TradeStatisticsPlatformParam convertTradeStatisticsPlatformParam(TradeStatisticsPlatformReq req);

    TradeStatisticsChannelParam convertTradeStatisticsChannelParam(TradeStatisticsChannelReq req);

    TradeStatisticsMerchantParam convertTradeStatisticsMerchantParam(TradeStatisticsMerchantReq req);

}
