package com.paysphere.controller.sandbox;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.SandBoxTradeCashOrderCmdService;
import com.paysphere.command.cmd.SandboxTradeForceSuccessCommand;
import com.paysphere.command.cmd.TradeCashCommand;
import com.paysphere.command.dto.TradePayoutDTO;
import com.paysphere.controller.request.SandboxTradeCashOrderPageReq;
import com.paysphere.controller.request.SandboxTradeForceSuccessReq;
import com.paysphere.controller.request.TradeCashReq;
import com.paysphere.controller.response.TradeCashVO;
import com.paysphere.convert.SandboxTradeConverter;
import com.paysphere.enums.TradeCashSourceEnum;
import com.paysphere.query.SandBoxTradeQueryService;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.dto.SandboxTradeCashOrderPageDTO;
import com.paysphere.query.param.SandboxTradeCashOrderPageParam;
import com.paysphere.result.PageResult;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * 沙箱 代付交易API
 *
 * @author West.Hu
 */
@Slf4j
@RestController
public class SandBoxTradeCashController {

    @Resource
    SandboxTradeConverter sandboxTradeConverter;
    @Resource
    SandBoxTradeQueryService sandBoxTradeQueryService;
    @Resource
    SandBoxTradeCashOrderCmdService sandBoxTradeCashOrderCmdService;

    /**
     * 沙箱 API代付
     */
    @PostMapping("/sandbox/v1/cash")
    public Mono<Result<TradeCashVO>> sandboxCash(@RequestBody @Validated TradeCashReq req) {
        log.info("sandboxCash req={}", JSONUtil.toJsonStr(req));
        TradeCashCommand command = sandboxTradeConverter.convertTradeCashCommand(req);
        command.setTradeCashSource(TradeCashSourceEnum.API);

        TradePayoutDTO dto = sandBoxTradeCashOrderCmdService.executeSandboxCash(command);
        return Mono.just(Result.ok(sandboxTradeConverter.convertTradeCashVO(dto)));
    }

    /**
     * 沙箱 API代付-强制成功/失败
     */
    @PostMapping("/sandbox/v1/cashForceSuccessOrFailed")
    public Mono<Result<Boolean>> sandboxCashForceSuccessOrFailed(@RequestBody @Validated
                                                                     SandboxTradeForceSuccessReq req) {
        log.info("cashForceSuccessOrFailed req={}", JSONUtil.toJsonStr(req));
        SandboxTradeForceSuccessCommand command = sandboxTradeConverter.convertSandboxTradeForceSuccessCommand(req);

        boolean successOrFailed = sandBoxTradeCashOrderCmdService.sandboxCashForceSuccessOrFailed(command);
        return Mono.just(Result.ok(successOrFailed));
    }

    /**
     * 沙箱 分页查询沙箱代付单列表
     */
    @PostMapping("sandbox/v1/pageCashOrderList")
    public Mono<PageResult<SandboxTradeCashOrderPageDTO>> pageSandboxCashOrderList(@RequestBody @Validated
                                                                                       SandboxTradeCashOrderPageReq req) {
        log.info("pageSandboxCashOrderList req={}", JSONUtil.toJsonStr(req));
        SandboxTradeCashOrderPageParam param = sandboxTradeConverter.convertSandboxTradeCashOrderPageParam(req);

        PageDTO<SandboxTradeCashOrderPageDTO> pageDTO = sandBoxTradeQueryService.pageSandboxCashOrderList(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

}
