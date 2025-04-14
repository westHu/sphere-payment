package api.sphere.controller.front.merchant.sandbox;


import api.sphere.convert.SandboxTradeConverter;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import app.sphere.command.SandBoxTradePayOrderCmdService;
import app.sphere.command.cmd.TradePaymentLinkCmd;
import api.sphere.controller.request.TradePaymentLinkPageReq;
import api.sphere.controller.request.TradePaymentLinkReq;
import infrastructure.sphere.db.entity.SandboxTradePaymentLinkOrder;
import app.sphere.query.SandBoxTradeQueryService;
import app.sphere.query.dto.SandboxTradePaymentLinkOrderPageDTO;
import app.sphere.query.param.TradePaymentLinkPageParam;
import share.sphere.result.PageResult;
import share.sphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * 支付链接API
 */
@Slf4j
@RestController
public class SandboxTradePaymentLinkController {

    @Resource
    SandboxTradeConverter sandboxTradeConverter;
    @Resource
    SandBoxTradePayOrderCmdService sandBoxTradePayOrderCmdService;
    @Resource
    SandBoxTradeQueryService sandBoxTradeQueryService;


    /**
     * 沙箱 PaymentLink收款
     */
    @PostMapping("/sandbox/v1/createPaymentLink")
    public Mono<Result<String>> createSandboxPaymentLink(@RequestBody @Validated TradePaymentLinkReq req) {
        log.info("createSandboxPaymentLink req={}", JSONUtil.toJsonStr(req));
        TradePaymentLinkCmd command = sandboxTradeConverter.convertTradePaymentLinkCommand(req);

        String sandboxPaymentLink = sandBoxTradePayOrderCmdService.executeSandboxPaymentLink(command);
        return Mono.just(Result.ok(sandboxPaymentLink));
    }


    /**
     * 沙箱 分页查询PaymentLink收款
     */
    @PostMapping("/sandbox/v1/pagePaymentLinkList")
    public Mono<PageResult<SandboxTradePaymentLinkOrderPageDTO>> pageSandboxPaymentLinkList(@RequestBody @Validated
                                                                                                TradePaymentLinkPageReq req) {
        log.info("pageSandboxPaymentLinkList req={}", JSONUtil.toJsonStr(req));
        TradePaymentLinkPageParam param = sandboxTradeConverter.convertTradePaymentLinkPageParam(req);
        
        Page<SandboxTradePaymentLinkOrder> page = sandBoxTradeQueryService.pageSandboxPaymentLinkList(param);
        List<SandboxTradePaymentLinkOrderPageDTO> voList = sandboxTradeConverter.convertSandboxTradePaymentLinkOrderPageDTOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

}
