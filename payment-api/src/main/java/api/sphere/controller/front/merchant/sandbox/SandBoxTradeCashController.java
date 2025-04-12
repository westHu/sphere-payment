package api.sphere.controller.front.merchant.sandbox;

import api.sphere.controller.request.TradePayoutReq;
import api.sphere.controller.response.TradePayoutVO;
import api.sphere.convert.SandboxTradeConverter;
import cn.hutool.json.JSONUtil;
import app.sphere.command.SandBoxTradeCashOrderCmdService;
import app.sphere.command.cmd.SandboxTradeForceSuccessCommand;
import app.sphere.command.cmd.TradePayoutCommand;
import app.sphere.command.dto.TradePayoutDTO;
import api.sphere.controller.request.SandboxTradeCashOrderPageReq;
import api.sphere.controller.request.SandboxTradeForceSuccessReq;
import share.sphere.enums.TradePayoutSourceEnum;
import app.sphere.query.SandBoxTradeQueryService;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.SandboxTradeCashOrderPageDTO;
import app.sphere.query.param.SandboxTradeCashOrderPageParam;
import share.sphere.result.PageResult;
import share.sphere.result.Result;
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
    public Mono<Result<TradePayoutVO>> sandboxCash(@RequestBody @Validated TradePayoutReq req) {
        log.info("sandboxCash req={}", JSONUtil.toJsonStr(req));
        TradePayoutCommand command = sandboxTradeConverter.convertTradeCashCommand(req);
        command.setTradePayoutSourceEnum(TradePayoutSourceEnum.API);

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
