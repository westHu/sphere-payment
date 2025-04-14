package app.sphere.query;

import app.sphere.query.dto.*;
import app.sphere.query.param.*;

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
