package api.sphere.controller.api.callback;

import app.sphere.command.PaymentCallBackCmdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import share.sphere.result.Result;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/callback/mock")
public class CallBack4MockController {

    @Resource
    PaymentCallBackCmdService paymentCallBackCmdService;

    /**
     * mock 收款回调
     */
    @PostMapping(value = "/transaction")
    public Mono<Result<String>> callBackTransaction4Mock(@RequestBody String req) {
        log.info("mockTransactionCallBack req={}", req);
        String result = paymentCallBackCmdService.callBackTransaction4Mock(req);
        return Mono.just(Result.ok(result));
    }

    /**
     * mock 出款回调
     */
    @PostMapping(value = "/disbursement")
    public Mono<Result<String>> callBackDisbursement4Mock(@RequestBody String req) {
        log.info("mockDisbursementCallBack req={}", req);
        String result = paymentCallBackCmdService.callBackDisbursement4Mock(req);
        return Mono.just(Result.ok(result));
    }


}
