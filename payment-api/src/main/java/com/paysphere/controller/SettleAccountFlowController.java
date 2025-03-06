package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.controller.request.SettleAccountFlowPageReq;
import com.paysphere.controller.response.SettleAccountFlowVO;
import com.paysphere.convert.SettleFlowConverter;
import com.paysphere.db.entity.SettleAccountFlow;
import com.paysphere.query.SettleFlowQueryService;
import com.paysphere.query.param.SettleAccountFlowPageParam;
import com.paysphere.result.PageResult;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;


@Slf4j
@RestController
public class SettleAccountFlowController {

    @Resource
    SettleFlowConverter settleFlowConverter;
    @Resource
    SettleFlowQueryService settleFlowQueryService;

    /**
     * 分页查询商户资金流水
     */
    @PostMapping("/v1/pageAccountFlowList")
    public Mono<PageResult<SettleAccountFlowVO>> pageAccountFlow(@RequestBody @Validated SettleAccountFlowPageReq req) {
        log.info("pageAccountFlow req={}", JSONUtil.toJsonStr(req));
        SettleAccountFlowPageParam param = settleFlowConverter.convertAccountFlowPageParam(req);

        Page<SettleAccountFlow> page = settleFlowQueryService.pageAccountFlowList(param);
        List<SettleAccountFlowVO> voList = settleFlowConverter.convertAccountFlowVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出商户资金流水
     */
    @PostMapping("/v1/exportAccountFlowList")
    public Mono<Result<String>> exportAccountFlowList(@RequestBody @Validated SettleAccountFlowPageReq req) {
        log.info("exportAccountFlowList req={}", JSONUtil.toJsonStr(req));
        SettleAccountFlowPageParam param = settleFlowConverter.convertAccountFlowPageParam(req);

        String exportAccountFlowList = settleFlowQueryService.exportAccountFlowList(param);
        return Mono.just(Result.ok(exportAccountFlowList));
    }



    //-------------------------------------------------------------------------------------------------------------

}
