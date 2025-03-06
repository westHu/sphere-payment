package com.paysphere.convert;


import com.paysphere.command.cmd.PaymentMethodStatusCommand;
import com.paysphere.command.cmd.PaymentMethodUpdateCommand;
import com.paysphere.controller.request.PaymentMethodPageReq;
import com.paysphere.controller.request.PaymentMethodReq;
import com.paysphere.controller.request.PaymentMethodStatusReq;
import com.paysphere.controller.request.PaymentMethodUpdateReq;
import com.paysphere.controller.response.PaymentMethodDropVO;
import com.paysphere.controller.response.PaymentMethodVO;
import com.paysphere.db.entity.PaymentMethod;
import com.paysphere.query.param.PaymentMethodPageParam;
import com.paysphere.query.param.PaymentMethodParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface PaymentMethodConverter {

    PaymentMethodParam convertPaymentMethodParam(PaymentMethodReq req);

    List<PaymentMethodVO> convertPaymentMethodVOList(List<PaymentMethod> dtoList);

    PaymentMethodVO convertPaymentMethodVO(PaymentMethod method);

    PaymentMethodPageParam convertPaymentMethodPageParam(PaymentMethodPageReq req);

    PaymentMethodUpdateCommand convertMethodUpdateCommand(PaymentMethodUpdateReq req);

    PaymentMethodStatusCommand convertPaymentMethodStatusCommand(PaymentMethodStatusReq req);

    List<PaymentMethodDropVO> convertPaymentMethodDropVOList(List<PaymentMethod> methodList);
}
