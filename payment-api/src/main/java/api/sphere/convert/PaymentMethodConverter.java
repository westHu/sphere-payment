package api.sphere.convert;


import api.sphere.controller.request.*;
import api.sphere.controller.response.PaymentMethodDropVO;
import api.sphere.controller.response.PaymentMethodVO;
import app.sphere.command.cmd.PaymentMethodStatusCommand;
import app.sphere.command.cmd.PaymentMethodUpdateCommand;
import app.sphere.query.param.PaymentMethodPageParam;
import app.sphere.query.param.PaymentMethodParam;
import infrastructure.sphere.db.entity.PaymentMethod;
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
