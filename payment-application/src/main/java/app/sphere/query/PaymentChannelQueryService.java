package app.sphere.query;

import app.sphere.query.dto.PaymentChannelDropDTO;
import app.sphere.query.param.PaymentChannelDropParam;
import app.sphere.query.param.PaymentChannelListParam;
import app.sphere.query.param.PaymentChannelPageParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.PaymentChannel;

import java.util.List;

public interface PaymentChannelQueryService {

//    List<PaymentChannelDropDTO> dropPaymentChannelList(PaymentChannelDropParam dropParam);

    Page<PaymentChannel> pagePaymentChannelList(PaymentChannelPageParam param);

    List<PaymentChannel> getPaymentChannelList(PaymentChannelListParam param);

}
