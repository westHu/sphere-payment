package com.paysphere.query;

import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.dto.TradeChannelDailySnapchatDTO;
import com.paysphere.query.dto.TradeMerchantDailySnapchatDTO;
import com.paysphere.query.dto.TradeMerchantStatisticsByAgentDTO;
import com.paysphere.query.dto.TradeMerchantStatisticsDTO;
import com.paysphere.query.dto.TradePlatformDailySnapchatDTO;
import com.paysphere.query.dto.TradeStatisticsAgentDTO;
import com.paysphere.query.dto.TradeTimelyStatisticsIndexDTO;
import com.paysphere.query.dto.TransferDailySnapchatDTO;
import com.paysphere.query.param.TradeMerchantStatisticsSnapshotParam;
import com.paysphere.query.param.TradeStatisticsAgentPageParam;
import com.paysphere.query.param.TradeStatisticsByAgentPageParam;
import com.paysphere.query.param.TradeStatisticsChannelParam;
import com.paysphere.query.param.TradeStatisticsMerchantParam;
import com.paysphere.query.param.TradeStatisticsPlatformParam;
import com.paysphere.query.param.TradeStatisticsTransferParam;
import com.paysphere.query.param.TradeTimelyStatisticsIndexParam;

public interface SnapshotTradeStatisticsQueryService {

    PageDTO<TradePlatformDailySnapchatDTO> getPlatformTradeStatistics(TradeStatisticsPlatformParam param);

    String exportPlatformTradeStatistics(TradeStatisticsPlatformParam param);

    PageDTO<TradeChannelDailySnapchatDTO> getChannelTradeStatistics(TradeStatisticsChannelParam param);

    String exportChannelTradeStatistics(TradeStatisticsChannelParam param);

    PageDTO<TradeMerchantDailySnapchatDTO> getMerchantTradeStatistics(TradeStatisticsMerchantParam param);

    String exportMerchantTradeStatistics(TradeStatisticsMerchantParam param);

    PageDTO<TransferDailySnapchatDTO> getTransferStatistics(TradeStatisticsTransferParam param);

    String exportTransferStatistics(TradeStatisticsTransferParam param);

    TradeMerchantStatisticsDTO getMerchantTradeStatistics4Index(TradeMerchantStatisticsSnapshotParam param);

    TradeTimelyStatisticsIndexDTO getTradeTimelyStatistics4Index(TradeTimelyStatisticsIndexParam param);

    PageDTO<TradeStatisticsAgentDTO> pageAgentTradeStatistics(TradeStatisticsAgentPageParam param);

    String exportAgentTradeStatistics(TradeStatisticsAgentPageParam param);

    PageDTO<TradeMerchantStatisticsByAgentDTO> pageMerchantTradeStatisticsByAgent(TradeStatisticsByAgentPageParam param);

    String exportMerchantTradeStatisticsByAgent(TradeStatisticsByAgentPageParam param);
}
