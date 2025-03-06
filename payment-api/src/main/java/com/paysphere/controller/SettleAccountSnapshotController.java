package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.paysphere.controller.request.SettleAccountSnapshotReq;
import com.paysphere.convert.SettleAccountSnapshotConverter;
import com.paysphere.query.SettleAccountSnapshotQueryService;
import com.paysphere.query.dto.AccountSnapshotDTO;
import com.paysphere.query.param.SettleAccountSnapshotParam;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
public class SettleAccountSnapshotController {

    @Resource
    SettleAccountSnapshotConverter settleAccountSnapshotConverter;
    @Resource
    SettleAccountSnapshotQueryService settleAccountSnapshotQueryService;

    /**
     * 查询商户余额及其快照（首页） - 商户平台
     */
    @PostMapping("/v1/getAccountSnapshot")
    public Mono<Result<AccountSnapshotDTO>> getAccountSnapshot(@RequestBody @Validated SettleAccountSnapshotReq req) {
        log.info("getAccountSnapshot req={}", JSONUtil.toJsonStr(req));
        SettleAccountSnapshotParam param = settleAccountSnapshotConverter.convertAccountSnapshotParam(req);

        AccountSnapshotDTO accountSnapshot = settleAccountSnapshotQueryService.getAccountSnapshot(param);
        return Mono.just(Result.ok(accountSnapshot));
    }
    

}
