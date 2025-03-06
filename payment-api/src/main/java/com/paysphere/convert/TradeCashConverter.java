package com.paysphere.convert;


import com.paysphere.command.cmd.TradeCashCommand;
import com.paysphere.command.cmd.TradeCashRefundCommand;
import com.paysphere.command.cmd.TradeCashSupplementCommand;
import com.paysphere.command.dto.TradePayoutDTO;
import com.paysphere.controller.request.TradeCashOrderPageReq;
import com.paysphere.controller.request.TradeCashRefundReq;
import com.paysphere.controller.request.TradeCashReq;
import com.paysphere.controller.request.TradeCashSupplementReq;
import com.paysphere.controller.response.TradeCashOrderPageVO;
import com.paysphere.controller.response.TradeCashVO;
import com.paysphere.query.dto.TradeCashOrderPageDTO;
import com.paysphere.query.param.TradeCashOrderPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradeCashConverter {

    TradeCashCommand convertTradeCashCommand(TradeCashReq req);

    TradeCashVO convertTradeCashVO(TradePayoutDTO dto);

    TradeCashOrderPageParam convertPageParam(TradeCashOrderPageReq req);

    List<TradeCashOrderPageVO> convertPageVOList(List<TradeCashOrderPageDTO> orderList);

    TradeCashSupplementCommand convertTradeCashSupplementCommand(TradeCashSupplementReq req);

    TradeCashRefundCommand convertTradeCashRefundCommand(TradeCashRefundReq req);

}
