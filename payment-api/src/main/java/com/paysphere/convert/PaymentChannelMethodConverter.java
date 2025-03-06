package com.paysphere.convert;


import com.paysphere.command.cmd.PaymentChannelMethodAddCommand;
import com.paysphere.command.cmd.PaymentChannelMethodStatusCommand;
import com.paysphere.command.cmd.PaymentChannelMethodUpdateCommand;
import com.paysphere.controller.request.PaymentChannelMethodAddReq;
import com.paysphere.controller.request.PaymentChannelMethodGroupReq;
import com.paysphere.controller.request.PaymentChannelMethodPageReq;
import com.paysphere.controller.request.PaymentChannelMethodRangeReq;
import com.paysphere.controller.request.PaymentChannelMethodReq;
import com.paysphere.controller.request.PaymentChannelMethodStatusReq;
import com.paysphere.controller.request.PaymentChannelMethodUpdateReq;
import com.paysphere.controller.response.PaymentChannelMethodVO;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.query.param.PaymentChannelMethodGroupParam;
import com.paysphere.query.param.PaymentChannelMethodPageParam;
import com.paysphere.query.param.PaymentChannelMethodParam;
import com.paysphere.query.param.PaymentChannelMethodRangeParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface PaymentChannelMethodConverter {

    PaymentChannelMethodVO convertPaymentChannelMethodVO(PaymentChannelMethod channelMethod);

    PaymentChannelMethodParam convertPaymentChannelMethodParam(PaymentChannelMethodReq req);

    List<PaymentChannelMethodVO> convertPaymentChannelMethodVOList(List<PaymentChannelMethod> data);

    PaymentChannelMethodPageParam convertPaymentChannelMethodPageParam(PaymentChannelMethodPageReq req);

    PaymentChannelMethodUpdateCommand convertPaymentChannelMethodUpdateCommand(PaymentChannelMethodUpdateReq req);

    PaymentChannelMethodAddCommand convertPaymentChannelMethodAddCommand(PaymentChannelMethodAddReq req);

    List<PaymentChannelMethodStatusCommand> convertPaymentChannelMethodStatusCommand(List<PaymentChannelMethodStatusReq> reqList);

    PaymentChannelMethodGroupParam convertPaymentChannelMethodGroupParam(PaymentChannelMethodGroupReq req);

    PaymentChannelMethodRangeParam convertPaymentChannelMethodRangeParam(PaymentChannelMethodRangeReq req);
}
