package app.sphere.query;

import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.TradeChannelDailySnapchatDTO;
import app.sphere.query.dto.TradeMerchantDailySnapchatDTO;
import app.sphere.query.dto.TradeMerchantStatisticsDTO;
import app.sphere.query.dto.TradePlatformDailySnapchatDTO;
import app.sphere.query.dto.TradeTimelyStatisticsIndexDTO;
import app.sphere.query.param.TradeMerchantStatisticsSnapshotParam;
import app.sphere.query.param.TradeStatisticsChannelParam;
import app.sphere.query.param.TradeStatisticsMerchantParam;
import app.sphere.query.param.TradeStatisticsPlatformParam;
import app.sphere.query.param.TradeTimelyStatisticsIndexParam;

public interface SnapshotTradeStatisticsQueryService {

    PageDTO<TradePlatformDailySnapchatDTO> getPlatformTradeStatistics(TradeStatisticsPlatformParam param);

    String exportPlatformTradeStatistics(TradeStatisticsPlatformParam param);

    PageDTO<TradeChannelDailySnapchatDTO> getChannelTradeStatistics(TradeStatisticsChannelParam param);

    String exportChannelTradeStatistics(TradeStatisticsChannelParam param);

    PageDTO<TradeMerchantDailySnapchatDTO> getMerchantTradeStatistics(TradeStatisticsMerchantParam param);

    String exportMerchantTradeStatistics(TradeStatisticsMerchantParam param);

    TradeMerchantStatisticsDTO getMerchantTradeStatistics4Index(TradeMerchantStatisticsSnapshotParam param);

    TradeTimelyStatisticsIndexDTO getTradeTimelyStatistics4Index(TradeTimelyStatisticsIndexParam param);


}
