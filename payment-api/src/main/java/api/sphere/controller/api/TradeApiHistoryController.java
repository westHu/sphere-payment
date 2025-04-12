package api.sphere.controller.api;

import api.sphere.controller.request.TradeOrderStatusInquiryReq;
import api.sphere.convert.TradeHistoryConverter;
import cn.hutool.json.JSONUtil;
import app.sphere.query.TradeHistoryService;
import app.sphere.query.dto.TradeOrderStatusInquiryDTO;
import app.sphere.query.param.TradeOrderStatusInquiryParam;
import share.sphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * 历史记录 API接口
 */
@Slf4j
@RestController
public class TradeApiHistoryController {

    @Resource
    TradeHistoryConverter tradeHistoryConverter;
    @Resource
    TradeHistoryService tradeHistoryService;

    /**
     * 历史记录查询状态 根据商户订单号
     */
    @PostMapping("/v1/inquiryOrderStatus")
    public Mono<Result<TradeOrderStatusInquiryDTO>> inquiryOrderStatus(@RequestBody @Validated TradeOrderStatusInquiryReq req) {
        log.info("inquiryOrderStatus req={}", JSONUtil.toJsonStr(req));
        TradeOrderStatusInquiryParam param = tradeHistoryConverter.convertTradeOrderStatusInquiryParam(req);

        TradeOrderStatusInquiryDTO statusInquiryDTO = tradeHistoryService.inquiryOrderStatus(param);
        return Mono.just(Result.ok(statusInquiryDTO));
    }

}
