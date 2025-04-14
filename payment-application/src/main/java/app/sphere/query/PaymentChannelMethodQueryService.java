package app.sphere.query;


import app.sphere.query.dto.*;
import app.sphere.query.param.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.PaymentChannelMethod;

import java.util.List;

public interface PaymentChannelMethodQueryService {

    List<PaymentChannelMethodGroupDTO> groupPaymentChannelMethodList(PaymentChannelMethodGroupParam param);

    List<ChannelPaymentMethodGroupDTO> groupChannelPaymentMethodList(PaymentChannelMethodGroupParam param);

    Page<PaymentChannelMethod> pagePaymentChannelMethodList(PaymentChannelMethodPageParam param);

    PaymentChannelMethod getPaymentChannelMethod(PaymentChannelMethodParam param);

    PaymentChannelMethodFeeRangeDTO getPaymentChannelMethodFeeRange(PaymentChannelMethodRangeParam param);
}
