package api.sphere.trade;

import cn.hutool.json.JSONUtil;
import start.sphere.PaymentApplication;
import app.sphere.command.cmd.TradePaymentCmd;
import api.sphere.controller.request.MerchantReq;
import api.sphere.controller.request.MoneyReq;
import api.sphere.controller.request.PayerReq;
import api.sphere.controller.request.ReceiverReq;
import api.sphere.controller.request.TradePaymentReq;
import api.sphere.convert.TradePayConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentApplication.class)
public class MapStrutsTest {

    @Resource
    TradePayConverter tradePayConverter;

    @Test
    public void sayHelloMqTest() {
        log.info("attest");
        log.info("attest");
    }

    @Test
    public void tradePayConverterTest() {
        log.info("attest");
        TradePaymentReq req = new TradePaymentReq();

        req.setOrderNo("34328742983749237423");
        req.setPurpose("test-p");
        req.setPaymentMethod("016");

        MoneyReq moneyReq = new MoneyReq();
        moneyReq.setAmount(BigDecimal.TEN);
        moneyReq.setCurrency("IDR");
        req.setMoney(moneyReq);

        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setMerchantId("13328478923748");
        merchantReq.setMerchantName("test name");
        req.setMerchant(merchantReq);

        PayerReq payerReq = new PayerReq();
        payerReq.setName("Test");
        payerReq.setEmail("test@test.com");
        payerReq.setPhone("+73-38347374");
        payerReq.setAddress("juju.strata");
        payerReq.setIdentity("472394798234798234");
        req.setPayer(payerReq);

        ReceiverReq receiverReq = new ReceiverReq();
        receiverReq.setName("Test-01");
        receiverReq.setEmail("test@test.com");
        receiverReq.setPhone("+73-38347374");
        receiverReq.setAddress("juju.strata");
        receiverReq.setIdentity("472394798234798234");
        req.setReceiver(receiverReq);

        log.info("req={}", JSONUtil.toJsonStr(req));

        TradePaymentCmd tradePaymentCmd = tradePayConverter.convertTradePayCmd(req);

        log.info("command={}", JSONUtil.toJsonStr(tradePaymentCmd));
    }

}
