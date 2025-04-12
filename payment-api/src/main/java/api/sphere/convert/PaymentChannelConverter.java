package api.sphere.convert;

import app.sphere.command.cmd.PaymentChannelStatusCommand;
import app.sphere.command.cmd.PaymentChannelUpdateCommand;
import api.sphere.controller.request.PaymentChannelListReq;
import api.sphere.controller.request.PaymentChannelPageReq;
import api.sphere.controller.request.PaymentChannelStatusReq;
import api.sphere.controller.request.PaymentChannelUpdateReq;
import api.sphere.controller.response.PaymentChannelVO;
import app.sphere.query.dto.PaymentChannelDropDTO;
import infrastructure.sphere.db.entity.PaymentChannel;
import app.sphere.query.param.PaymentChannelListParam;
import app.sphere.query.param.PaymentChannelPageParam;
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
