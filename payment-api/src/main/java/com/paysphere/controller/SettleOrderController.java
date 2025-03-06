package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.controller.request.SettleOrderPageReq;
import com.paysphere.controller.request.SettleOrderReq;
import com.paysphere.convert.SettleConverter;
import com.paysphere.db.entity.SettleOrder;
import com.paysphere.query.SettleOrderQueryService;
import com.paysphere.query.dto.SettleOrderDTO;
import com.paysphere.query.param.SettleOrderPageParam;
import com.paysphere.query.param.SettleOrderParam;
import com.paysphere.result.PageResult;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 结算API
 */
@Slf4j
@RestController
public class SettleOrderController {

    @Resource
    SettleConverter settleConverter;
    @Resource
    SettleOrderQueryService settleOrderQueryService;


    /**
     * 分页查询结算数据
     */
    @PostMapping("/v1/pageSettleOrderList")
    public Mono<PageResult<SettleOrderDTO>> pageSettleOrderList(@RequestBody SettleOrderPageReq req) {
        log.info("pageSettleOrderList req={}", JSONUtil.toJsonStr(req));
        SettleOrderPageParam param = settleConverter.convertSettleOrderPageParam(req);

        Page<SettleOrder> page = settleOrderQueryService.pageSettleOrderList(param);
        List<SettleOrderDTO> voList = settleConverter.convertSettleOrderDTOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 查询结算数据
     */
    @PostMapping("/v1/getSettleOrder")
    public Mono<Result<SettleOrderDTO>> getSettleOrder(@RequestBody SettleOrderReq req) {
        log.info("getSettleOrder req={}", JSONUtil.toJsonStr(req));
        SettleOrderParam param = settleConverter.convertSettleOrderParam(req);

        SettleOrder settleOrder = settleOrderQueryService.getSettleOrder(param);
        SettleOrderDTO dto = settleConverter.convertSettleOrderDTO(settleOrder);
        return Mono.just(Result.ok(dto));
    }

}
