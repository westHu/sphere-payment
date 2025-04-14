package api.sphere.controller.api;

import api.sphere.controller.request.TradePayoutReq;
import api.sphere.controller.response.TradePayoutVO;
import api.sphere.convert.TradePayoutConverter;
import app.sphere.command.TradePayoutOrderCmdService;
import app.sphere.command.cmd.TradePayoutCommand;
import app.sphere.command.dto.TradePayoutDTO;
import app.sphere.query.TradePayoutOrderQueryService;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import share.sphere.enums.TradePayoutSourceEnum;
import share.sphere.result.Result;


/**
 * 代付交易API
 *
 * @author West.Hu
 * udpate 22.4
 */
@Slf4j
@RestController
public class TradeApiPayoutOrderController {

    @Resource
    TradePayoutOrderQueryService tradePayoutOrderQueryService;
    @Resource
    TradePayoutOrderCmdService tradePayoutOrderCmdService;
    @Resource
    TradePayoutConverter tradePayoutConverter;

    /**
     * 代付
     */
    @PostMapping("/v1/payout")
    public Mono<Result<TradePayoutVO>> payout(@RequestBody @Validated TradePayoutReq req) {
        log.info("payout req={}", JSONUtil.toJsonStr(req));
        TradePayoutCommand command = tradePayoutConverter.convertTradePayoutCommand(req);
        command.setTradePayoutSourceEnum(TradePayoutSourceEnum.API);

        TradePayoutDTO tradePayoutDTO = tradePayoutOrderCmdService.executePayout(command);
        TradePayoutVO tradePayoutVO = tradePayoutConverter.convertTradePayoutVO(tradePayoutDTO);
        return Mono.just(Result.ok(tradePayoutVO));
    }


}
