package com.paysphere.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.PaymentChannel;
import com.paysphere.query.param.PaymentChannelListParam;
import com.paysphere.query.param.PaymentChannelPageParam;


import java.util.List;

public interface PaymentChannelQueryService {

    Page<PaymentChannel> pagePaymentChannelList(PaymentChannelPageParam param);

    List<PaymentChannel> getPaymentChannelList(PaymentChannelListParam param);
}
