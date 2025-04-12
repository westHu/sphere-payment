package api.sphere.trade;

import start.sphere.PaymentApplication;
import api.sphere.controller.api.TradeApiPaymentOrderController;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentApplication.class)
public class TradeTest {

    @Resource
    TradeApiPaymentOrderController tradePayOrderController;

    @Test
    public void sayHelloMqTest() {
        log.info("attest");
    }


}
