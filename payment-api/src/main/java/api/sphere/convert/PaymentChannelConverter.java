package api.sphere.convert;

import api.sphere.controller.request.*;
import api.sphere.controller.response.PaymentChannelVO;
import app.sphere.command.cmd.PaymentChannelStatusCommand;
import app.sphere.command.cmd.PaymentChannelUpdateCommand;
import app.sphere.query.param.PaymentChannelListParam;
import app.sphere.query.param.PaymentChannelPageParam;
import infrastructure.sphere.db.entity.PaymentChannel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface PaymentChannelConverter {

    PaymentChannelPageParam convertPaymentChannelPageParam(PaymentChannelPageReq req);

    List<PaymentChannelVO> convertPaymentChannelVOList(List<PaymentChannel> dtoList);

    PaymentChannelUpdateCommand convertPaymentChannelUpdateCommand(PaymentChannelUpdateReq req);

    PaymentChannelStatusCommand convertPaymentChannelStatusCommand(PaymentChannelStatusReq req);

    PaymentChannelListParam convertPaymentChannelListParam(PaymentChannelListReq req);

    PaymentChannelVO paymentChannelToPaymentChannelVO(PaymentChannel channel);

}
