package infrastructure.sphere.remote.channel.mock;

import infrastructure.sphere.db.entity.*;
import infrastructure.sphere.remote.channel.*;
import infrastructure.sphere.remote.channel.mock.dto.MockDisbursementResultDTO;
import infrastructure.sphere.remote.channel.mock.dto.MockTransactionResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MockChannelService implements ChannelService {

    @Override
    public ChannelEnum getChannelName() {
        return ChannelEnum.C_MOCK;
    }

    @Override
    public ChannelResult<? extends BaseTransactionDTO> transaction(PaymentChannel paymentChannel,
                                                                   PaymentMethod paymentMethod,
                                                                   PaymentChannelMethod paymentChannelMethod,
                                                                   TradePaymentOrder order) {
        log.info("Jaya transaction order={}", order.getTradeNo());
        MockTransactionResultDTO transactionResultDTO = new MockTransactionResultDTO();
        transactionResultDTO.setTxId(UUID.randomUUID().toString());
        transactionResultDTO.setStatus(1);
        transactionResultDTO.setMessage(null);
        transactionResultDTO.setChannelOrderNo(transactionResultDTO.getTxId());
        return new ChannelResult<>(transactionResultDTO);
    }

    @Override
    public <P> ChannelResult<String> transactionCallBack(BaseCallBackDTO<P> callBackDTO) {
        return new ChannelResult<>(true, "SUCCESS");
    }

    @Override
    public ChannelResult<? extends BaseDisbursementDTO> disbursement(PaymentChannel channel, PaymentMethod paymentMethod, PaymentChannelMethod channelMethod, TradePayoutOrder order) {
        log.info("Jaya transaction order={}", order.getTradeNo());
        MockDisbursementResultDTO disbursementResultDTO = new MockDisbursementResultDTO();
        disbursementResultDTO.setTxId(UUID.randomUUID().toString());
        disbursementResultDTO.setStatus(1);
        disbursementResultDTO.setMessage(null);
        disbursementResultDTO.setChannelOrderNo(disbursementResultDTO.getTxId());
        return new ChannelResult<>(disbursementResultDTO);
    }

    @Override
    public <P> ChannelResult<?> disbursementCallBack(BaseCallBackDTO<P> callBackDTO) {
        return new ChannelResult<>(true, "SUCCESS");
    }

    @Override
    public ChannelResult<? extends BaseDisbursementDTO> withdraw(PaymentChannel channel, PaymentMethod paymentMethod, PaymentChannelMethod channelMethod, TradeWithdrawOrder order) {
        return ChannelService.super.withdraw(channel, paymentMethod, channelMethod, order);
    }

    @Override
    public <P> ChannelResult<?> withdrawCallBack(BaseCallBackDTO<P> callBackDTO) {
        return ChannelService.super.withdrawCallBack(callBackDTO);
    }

    @Override
    public ChannelResult<List<BaseInquiryBalanceDTO>> inquiryBalance() {
        return ChannelService.super.inquiryBalance();
    }
}
