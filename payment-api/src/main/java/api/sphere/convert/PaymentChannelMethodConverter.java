package api.sphere.convert;


import api.sphere.controller.request.PaymentChannelMethodAddReq;
import api.sphere.controller.request.PaymentChannelMethodGroupReq;
import api.sphere.controller.request.PaymentChannelMethodPageReq;
import api.sphere.controller.request.PaymentChannelMethodRangeReq;
import api.sphere.controller.request.PaymentChannelMethodReq;
import api.sphere.controller.request.PaymentChannelMethodStatusReq;
import api.sphere.controller.request.PaymentChannelMethodUpdateReq;
import api.sphere.controller.response.PaymentChannelMethodVO;
import app.sphere.command.cmd.PaymentChannelMethodAddCommand;
import app.sphere.command.cmd.PaymentChannelMethodStatusCommand;
import app.sphere.command.cmd.PaymentChannelMethodUpdateCommand;
import app.sphere.query.param.PaymentChannelMethodGroupParam;
import app.sphere.query.param.PaymentChannelMethodPageParam;
import app.sphere.query.param.PaymentChannelMethodParam;
import app.sphere.query.param.PaymentChannelMethodRangeParam;
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
