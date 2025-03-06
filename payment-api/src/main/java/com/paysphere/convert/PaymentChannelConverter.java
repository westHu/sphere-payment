package com.paysphere.convert;

import com.paysphere.command.cmd.PaymentChannelStatusCommand;
import com.paysphere.command.cmd.PaymentChannelUpdateCommand;
import com.paysphere.controller.request.PaymentChannelListReq;
import com.paysphere.controller.request.PaymentChannelPageReq;
import com.paysphere.controller.request.PaymentChannelStatusReq;
import com.paysphere.controller.request.PaymentChannelUpdateReq;
import com.paysphere.controller.response.PaymentChannelDropVO;
import com.paysphere.controller.response.PaymentChannelVO;
import com.paysphere.db.entity.PaymentChannel;
import com.paysphere.query.param.PaymentChannelListParam;
import com.paysphere.query.param.PaymentChannelPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface PaymentChannelConverter {

    PaymentChannelPageParam convertPaymentChannelPageParam(PaymentChannelPageReq req);

    List<PaymentChannelVO> convertPaymentChannelVOList(List<PaymentChannel> dtoList);

    PaymentChannelUpdateCommand convertPaymentChannelUpdateCommand(PaymentChannelUpdateReq req);

    PaymentChannelStatusCommand convertPaymentChannelStatusCommand(PaymentChannelStatusReq req);

    List<PaymentChannelDropVO> convertPaymentChannelDropVOList(List<PaymentChannel> channelList);

    PaymentChannelListParam convertPaymentChannelListParam(PaymentChannelListReq req);
}
