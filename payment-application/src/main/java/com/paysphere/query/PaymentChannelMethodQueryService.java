package com.paysphere.query;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.query.dto.ChannelPaymentMethodGroupDTO;
import com.paysphere.query.dto.PaymentChannelMethodFeeRangeDTO;
import com.paysphere.query.dto.PaymentChannelMethodGroupDTO;
import com.paysphere.query.param.PaymentChannelMethodGroupParam;
import com.paysphere.query.param.PaymentChannelMethodPageParam;
import com.paysphere.query.param.PaymentChannelMethodParam;
import com.paysphere.query.param.PaymentChannelMethodRangeParam;

import java.util.List;

public interface PaymentChannelMethodQueryService {

    List<PaymentChannelMethodGroupDTO> groupPaymentChannelMethodList(PaymentChannelMethodGroupParam param);

    List<ChannelPaymentMethodGroupDTO> groupChannelPaymentMethodList(PaymentChannelMethodGroupParam param);

    Page<PaymentChannelMethod> pagePaymentChannelMethodList(PaymentChannelMethodPageParam param);

    PaymentChannelMethod getPaymentChannelMethod(PaymentChannelMethodParam param);

    PaymentChannelMethodFeeRangeDTO getPaymentChannelMethodFeeRange(PaymentChannelMethodRangeParam param);
}
