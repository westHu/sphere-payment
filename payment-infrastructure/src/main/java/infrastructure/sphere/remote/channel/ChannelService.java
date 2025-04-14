package infrastructure.sphere.remote.channel;


import infrastructure.sphere.db.entity.PaymentChannel;
import infrastructure.sphere.db.entity.PaymentChannelMethod;
import infrastructure.sphere.db.entity.PaymentMethod;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;
import share.sphere.exception.PaymentException;

import java.util.List;

public interface ChannelService {

    /**
     * 指定渠道名称
     */
    default ChannelEnum getChannelName() {
        throw new PaymentException("No assign channel");
    }


    /**
     * 收款
     */
    default ChannelResult<? extends BaseTransactionDTO> transaction(PaymentChannel paymentChannel,
                                                                    PaymentMethod paymentMethod,
                                                                    PaymentChannelMethod paymentChannelMethod,
                                                                    TradePaymentOrder order) {
        return new ChannelResult<>("transaction exception");
    }

    /**
     * 收款回调
     */
    default <P> ChannelResult<?> transactionCallBack(BaseCallBackDTO<P> callBackDTO) {
        throw new PaymentException("TransactionCallBack exception");
    }


    //--------------------------------------------------------------------------------------------------------------

    /**
     * 出款
     */
    default ChannelResult<? extends BaseDisbursementDTO> disbursement(PaymentChannel channel,
                                                                      PaymentMethod paymentMethod,
                                                                      PaymentChannelMethod channelMethod,
                                                                      TradePayoutOrder order) {
        return new ChannelResult<>("Disbursement exception");
    }

    /**
     * 出款回调
     */
    default <P> ChannelResult<?> disbursementCallBack(BaseCallBackDTO<P> callBackDTO) {
        throw new PaymentException("DisbursementCallBack exception");
    }



    //--------------------------------------------------------------------------------------------------------------

    /**
     * 提现
     */
    default ChannelResult<? extends BaseDisbursementDTO> withdraw(PaymentChannel channel,
                                                                      PaymentMethod paymentMethod,
                                                                      PaymentChannelMethod channelMethod,
                                                                      TradeWithdrawOrder order) {
        return new ChannelResult<>("Disbursement exception");
    }

    /**
     * 提现回调
     */
    default <P> ChannelResult<?> withdrawCallBack(BaseCallBackDTO<P> callBackDTO) {
        throw new PaymentException("DisbursementCallBack exception");
    }


    //--------------------------------------------------------------------------------------------------------------

    /**
     * 查询对那个渠道中的 账户余额
     */
    default ChannelResult<List<BaseInquiryBalanceDTO>> inquiryBalance() {
        throw new PaymentException("inquiryBalance exception");
    }

}
