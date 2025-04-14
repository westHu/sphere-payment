package api.sphere.controller.front.merchant.sandbox;

import api.sphere.controller.request.TradeOrderStatusInquiryReq;
import api.sphere.convert.TradeHistoryConverter;
import app.sphere.query.SandboxTradeHistoryService;
import app.sphere.query.dto.TradeOrderStatusInquiryDTO;
import app.sphere.query.param.TradeOrderStatusInquiryParam;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import share.sphere.result.Result;


/**
 * 沙箱历史记录 API
 */
@Slf4j
@RestController
public class SandboxTradeHistoryController {

    @Resource
    TradeHistoryConverter tradeHistoryConverter;
    @Resource
    SandboxTradeHistoryService sandboxTradeHistoryService;

    /**
     * Gateway API历史记录查询状态 根据商户订单号
     */
    @PostMapping("/sandbox/v1/inquiryOrderStatus")
    public Mono<Result<TradeOrderStatusInquiryDTO>> inquirySandboxOrderStatus(@RequestBody @Validated
                                                                                  TradeOrderStatusInquiryReq req) {
        log.info("inquiryOrderStatus req={}", JSONUtil.toJsonStr(req));
        TradeOrderStatusInquiryParam param = tradeHistoryConverter.convertTradeOrderStatusInquiryParam(req);

        TradeOrderStatusInquiryDTO inquiryDTO = null;//sandboxTradeHistoryService.inquiryOrderStatus(param);
        return Mono.just(Result.ok(inquiryDTO));
    }

}
