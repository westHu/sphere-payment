package com.paysphere.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.db.entity.PaymentMethod;
import com.paysphere.query.param.PaymentMethodPageParam;
import com.paysphere.query.param.PaymentMethodParam;


import java.util.List;

public interface PaymentMethodQueryService {

    Page<PaymentMethod> pagePaymentMethodList(PaymentMethodPageParam param);

    PaymentMethod getPaymentMethod(PaymentMethodParam param);

    List<PaymentMethod> getPaymentMethodList4Transaction();

    List<PaymentMethod> getPaymentMethodList4Disbursement();

    PaymentChannelMethod getDisbursementPaymentChannelMethod(String channelCode, String paymentMethod);


}
