package api.sphere.controller.api;

import api.sphere.controller.request.TradePayoutReq;
import api.sphere.controller.response.TradePayoutVO;
import api.sphere.convert.TradeCashConverter;
import cn.hutool.json.JSONUtil;
import app.sphere.command.TradePayoutOrderCmdService;
import app.sphere.command.cmd.TradePayoutCommand;
import app.sphere.command.dto.TradePayoutDTO;
import share.sphere.enums.TradePayoutSourceEnum;
import app.sphere.query.TradePayoutOrderQueryService;
import share.sphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


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
    TradeCashConverter tradePayoutConverter;

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
