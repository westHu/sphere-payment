package com.paysphere.convert;


import com.paysphere.controller.request.TradeMerchantStatisticsSnapshotReq;
import com.paysphere.controller.request.TradeStatisticsAgentPageReq;
import com.paysphere.controller.request.TradeStatisticsByAgentPageReq;
import com.paysphere.controller.request.TradeStatisticsChannelReq;
import com.paysphere.controller.request.TradeStatisticsMerchantReq;
import com.paysphere.controller.request.TradeStatisticsPlatformReq;
import com.paysphere.controller.request.TradeStatisticsTransferReq;
import com.paysphere.controller.request.TradeTimelyStatisticsIndexReq;
import com.paysphere.query.param.TradeMerchantStatisticsSnapshotParam;
import com.paysphere.query.param.TradeStatisticsAgentPageParam;
import com.paysphere.query.param.TradeStatisticsByAgentPageParam;
import com.paysphere.query.param.TradeStatisticsChannelParam;
import com.paysphere.query.param.TradeStatisticsMerchantParam;
import com.paysphere.query.param.TradeStatisticsPlatformParam;
import com.paysphere.query.param.TradeStatisticsTransferParam;
import com.paysphere.query.param.TradeTimelyStatisticsIndexParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface SnapshotTradeStatisticsConverter {

    SnapshotTradeStatisticsConverter INSTANCE = Mappers.getMapper(SnapshotTradeStatisticsConverter.class);

    TradeMerchantStatisticsSnapshotParam convertTradeMerchantStatisticsSnapshotParam(TradeMerchantStatisticsSnapshotReq req);

    TradeTimelyStatisticsIndexParam convertTradeTimelyStatisticsIndexParam(TradeTimelyStatisticsIndexReq req);

    TradeStatisticsAgentPageParam convertTradeStatisticsAgentPageParam(TradeStatisticsAgentPageReq req);

    TradeStatisticsByAgentPageParam convertTradeStatisticsByAgentPageParam(TradeStatisticsByAgentPageReq req);

    TradeStatisticsPlatformParam convertTradeStatisticsPlatformParam(TradeStatisticsPlatformReq req);

    TradeStatisticsChannelParam convertTradeStatisticsChannelParam(TradeStatisticsChannelReq req);

    TradeStatisticsMerchantParam convertTradeStatisticsMerchantParam(TradeStatisticsMerchantReq req);

    TradeStatisticsTransferParam convertTradeStatisticsTransferParam(TradeStatisticsTransferReq req);
}
