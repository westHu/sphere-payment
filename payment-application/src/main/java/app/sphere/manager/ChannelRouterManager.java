package app.sphere.manager;


import app.sphere.manager.dto.ChannelRouterDTO;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import infrastructure.sphere.db.entity.PaymentChannel;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
import infrastructure.sphere.db.entity.PaymentMethod;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;
import share.sphere.enums.PaymentDirectionEnum;
import share.sphere.exception.PaymentException;
import domain.sphere.repository.PaymentChannelMethodRepository;
import domain.sphere.repository.PaymentChannelRepository;
import domain.sphere.repository.PaymentMethodRepository;
import share.sphere.utils.BinaryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static share.sphere.TradeConstant.LIMIT_1;

/**
 * 渠道路由
 */
@Slf4j
@Component
public class ChannelRouterManager {

    @Resource
    PaymentMethodRepository paymentMethodRepository;
    @Resource
    PaymentChannelMethodRepository paymentChannelMethodRepository;
    @Resource
    PaymentChannelRepository paymentChannelRepository;

    /**
     * 收单路由
     */
    public ChannelRouterDTO transactionRouter(TradePaymentOrder order) {
        String paymentMethod = order.getPaymentMethod();
        log.info("transactionRouter paymentMethod={}", paymentMethod);

        //校验渠支付方式合法性
        PaymentMethod method = verifyThenGetPaymentMethod(paymentMethod, PaymentDirectionEnum.TRANSACTION);

        //支付配置支持的渠道
        PaymentChannelMethod supportChannelMethod = getPaymentChannelMethodList(method, order.getChannelCode());
        if (Objects.isNull(supportChannelMethod)) {
            throw new PaymentException("No transaction channel & paymentMethod available");
        }

        //构建路由组合
        PaymentChannel channel = getPaymentChannel(supportChannelMethod.getChannelCode());
        if (Objects.isNull(channel)) {
            throw new PaymentException("No transaction channel available");
        }
        ChannelRouterDTO routerDTO = new ChannelRouterDTO();
        routerDTO.setPaymentChannel(channel);
        routerDTO.setPaymentMethod(method);
        routerDTO.setPaymentChannelMethod(supportChannelMethod);
        return routerDTO;
    }


    /**
     * 付款路由
     * 检查平台账户余额是否足够、限额限次
     */
    public ChannelRouterDTO disbursementRouter(TradePayoutOrder order) {
        String paymentMethod = order.getPaymentMethod();

        //校验渠支付方式合法性
        PaymentMethod method = verifyThenGetPaymentMethod(paymentMethod, PaymentDirectionEnum.DISBURSEMENT);

        //支付配置支持的渠道
        PaymentChannelMethod supportChannelMethod = getPaymentChannelMethodList(method, order.getChannelCode());
        if (Objects.isNull(supportChannelMethod)) {
            throw new PaymentException("No transaction channel & paymentMethod available");
        }

        //构建路由组合
        PaymentChannel channel = getPaymentChannel(supportChannelMethod.getChannelCode());
        if (Objects.isNull(channel)) {
            throw new PaymentException("No transaction channel available");
        }

        ChannelRouterDTO router = new ChannelRouterDTO();
        router.setPaymentChannel(channel);
        router.setPaymentMethod(method);
        router.setPaymentChannelMethod(supportChannelMethod);
        return router;
    }

    /**
     * 提现路由
     * 检查平台账户余额是否足够、限额限次
     */
    public ChannelRouterDTO withdrawRouter(TradeWithdrawOrder order) {
        String paymentMethod = order.getPaymentMethod();

        //校验渠支付方式合法性
        PaymentMethod method = verifyThenGetPaymentMethod(paymentMethod, PaymentDirectionEnum.DISBURSEMENT);

        //支付配置支持的渠道
        PaymentChannelMethod supportChannelMethod = getPaymentChannelMethodList(method, order.getChannelCode());
        if (Objects.isNull(supportChannelMethod)) {
            throw new PaymentException("No transaction channel & paymentMethod available");
        }

        //构建路由组合
        PaymentChannel channel = getPaymentChannel(supportChannelMethod.getChannelCode());
        if (Objects.isNull(channel)) {
            throw new PaymentException("No transaction channel available");
        }

        ChannelRouterDTO router = new ChannelRouterDTO();
        router.setPaymentChannel(channel);
        router.setPaymentMethod(method);
        router.setPaymentChannelMethod(supportChannelMethod);
        return router;
    }


    /**
     * 重点：渠道确定，选择最优的平台来调用该渠道，
     */
    private PaymentChannelMethod getPaymentChannelMethodList(PaymentMethod method, String channelCode) {
        //商户配置支持的支付方式和渠道列表
        String paymentMethod = method.getPaymentMethod();


        //查询渠道端的支付方式和渠道的绑定关系
        QueryWrapper<PaymentChannelMethod> channelMethodQuery = new QueryWrapper<>();
        channelMethodQuery.lambda()
                .eq(PaymentChannelMethod::getPaymentDirection, PaymentDirectionEnum.TRANSACTION.getCode())
                .eq(PaymentChannelMethod::getPaymentMethod, paymentMethod)
                .eq(PaymentChannelMethod::getChannelCode, channelCode)
                .eq(PaymentChannelMethod::isStatus, true)
                .last("LIMIT 1");
        return paymentChannelMethodRepository.getOne(channelMethodQuery);
    }

    /**
     * 校验并得到支付方式
     */
    public PaymentMethod verifyThenGetPaymentMethod(String paymentMethod, PaymentDirectionEnum paymentDirectionEnum) {
        QueryWrapper<PaymentMethod> methodQuery = new QueryWrapper<>();
        methodQuery.lambda().eq(PaymentMethod::getPaymentMethod, paymentMethod).last(LIMIT_1);
        PaymentMethod method = paymentMethodRepository.getOne(methodQuery);
        Assert.notNull(method, () -> new PaymentException("PaymentMethod does not exist: " + paymentMethod));

        //校验状态
        boolean status = method.isStatus();
        Assert.isTrue(status, () -> new PaymentException("PaymentMethod status is disable: " + paymentMethod));

        //校验该支付方式是否能够收款
        Integer paymentDirection = method.getPaymentDirection();
        List<Integer> cursor = BinaryUtil.find1Cursor(paymentDirection);
        boolean contains = cursor.contains(paymentDirectionEnum.getCode());
        Assert.isTrue(contains, () -> new PaymentException("PaymentMethod direction is illegal: " + paymentMethod));

        return method;
    }

    /**
     * 查询channel
     */
    private PaymentChannel getPaymentChannel(String channelCode) {
        QueryWrapper<PaymentChannel> channelQuery = new QueryWrapper<>();
        channelQuery.select("channel_code as channelCode, " +
                "channel_name as channelName, " +
                "channel_type as channelType, " +
                "url, " +
                "license," +
                "division," +
                "status");
        channelQuery.lambda().eq(PaymentChannel::getChannelCode, channelCode)
                .eq(PaymentChannel::isStatus, true)
                .last(LIMIT_1);
        return paymentChannelRepository.getOne(channelQuery);
    }
}
