package api.sphere.convert;


import app.sphere.command.cmd.PaymentMethodStatusCommand;
import app.sphere.command.cmd.PaymentMethodUpdateCommand;
import api.sphere.controller.request.PaymentMethodPageReq;
import api.sphere.controller.request.PaymentMethodReq;
import api.sphere.controller.request.PaymentMethodStatusReq;
import api.sphere.controller.request.PaymentMethodUpdateReq;
import api.sphere.controller.response.PaymentMethodDropVO;
import api.sphere.controller.response.PaymentMethodVO;
import infrastructure.sphere.db.entity.PaymentMethod;
import app.sphere.query.param.PaymentMethodPageParam;
import app.sphere.query.param.PaymentMethodParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface PaymentMethodConverter {

    PaymentMethodParam convertPaymentMethodParam(PaymentMethodReq req);

    List<PaymentMethodVO> convertPaymentMethodVOList(List<PaymentMethod> dtoList);

    PaymentMethodVO convertPaymentMethodVO(PaymentMethod method);

    PaymentMethodPageParam convertPaymentMethodPageParam(PaymentMethodPageReq req);

    PaymentMethodStatusCommand convertPaymentMethodStatusCommand(PaymentMethodStatusReq req);

    List<PaymentMethodDropVO> convertPaymentMethodDropVOList(List<PaymentMethod> methodList);

    PaymentMethodUpdateCommand convertPaymentMethodUpdateCommand(PaymentMethodUpdateReq req);
}
