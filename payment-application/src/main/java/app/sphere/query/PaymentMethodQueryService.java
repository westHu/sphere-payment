package app.sphere.query;

import app.sphere.query.param.PaymentMethodPageParam;
import app.sphere.query.param.PaymentMethodParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
import infrastructure.sphere.db.entity.PaymentMethod;

import java.util.List;

public interface PaymentMethodQueryService {

    Page<PaymentMethod> pagePaymentMethodList(PaymentMethodPageParam param);

    PaymentMethod getPaymentMethod(PaymentMethodParam param);

    List<PaymentMethod> getPaymentMethodList4Transaction();

    List<PaymentMethod> getPaymentMethodList4Disbursement();

    PaymentChannelMethod getDisbursementPaymentChannelMethod(String channelCode, String paymentMethod);


}
