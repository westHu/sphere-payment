package api.sphere.convert;


import api.sphere.controller.request.*;
import api.sphere.controller.response.PaymentChannelMethodVO;
import app.sphere.command.cmd.*;
import app.sphere.query.param.*;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
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

    PaymentChannelMethodGroupParam convertPaymentChannelMethodGroupParam(PaymentChannelMethodGroupReq req);

    PaymentChannelMethodRangeParam convertPaymentChannelMethodRangeParam(PaymentChannelMethodRangeReq req);

    PaymentChannelMethodStatusCommand convertPaymentChannelMethodStatusCommand(PaymentChannelMethodStatusReq req);
}
