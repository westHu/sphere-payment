package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.MerchantOperatorCmdService;
import com.paysphere.command.cmd.MerchantOperatorAddCmd;
import com.paysphere.command.cmd.MerchantOperatorUpdateCmd;
import com.paysphere.controller.request.MerchantOperatorAddReq;
import com.paysphere.controller.request.MerchantOperatorListReq;
import com.paysphere.controller.request.MerchantOperatorPageReq;
import com.paysphere.controller.request.MerchantOperatorUpdateReq;
import com.paysphere.controller.response.MerchantOperatorVO;
import com.paysphere.convert.MerchantOperatorConverter;
import com.paysphere.db.entity.MerchantOperator;
import com.paysphere.query.MerchantOperatorQueryService;
import com.paysphere.query.dto.MerchantOperatorDTO;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.param.MerchantOperatorListParam;
import com.paysphere.query.param.MerchantOperatorPageParam;
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


/**
 * 操作员 API
 */
@Slf4j
@RestController
public class MerchantOperatorController {

    @Resource
    MerchantOperatorConverter merchantOperatorConverter;
    @Resource
    MerchantOperatorQueryService merchantOperatorQueryService;
    @Resource
    MerchantOperatorCmdService merchantOperatorCmdService;


    /**
     * 分页查询商户操作员 商户平台
     */
    @PostMapping("/v1/pageMerchantOperatorList")
    public Mono<PageResult<MerchantOperatorDTO>> pageMerchantOperatorList(@RequestBody @Validated MerchantOperatorPageReq req) {
        log.info("pageMerchantOperatorList req={}", JSONUtil.toJsonStr(req));
        MerchantOperatorPageParam param = merchantOperatorConverter.convertMerchantOperatorPageParam(req);

        PageDTO<MerchantOperatorDTO> pageDTO = merchantOperatorQueryService.pageMerchantOperatorList(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    /**
     * 查询商户操作员 商户平台
     */
    @PostMapping("/v1/getMerchantOperatorList")
    public Mono<Result<List<MerchantOperatorVO>>> getMerchantOperatorList(@RequestBody @Validated MerchantOperatorListReq req) {
        log.info("getMerchantOperatorList req={}", JSONUtil.toJsonStr(req));
        MerchantOperatorListParam param = merchantOperatorConverter.convertMerchantOperatorListParam(req);

        List<MerchantOperator> operatorList = merchantOperatorQueryService.getMerchantOperatorList(param);
        List<MerchantOperatorVO> voList = merchantOperatorConverter.convertMerchantOperatorList(operatorList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 新增商户操作员 商户平台
     */
    @PostMapping("/v1/addMerchantOperator")
    public Mono<Result<Boolean>> addMerchantOperator(@RequestBody @Validated MerchantOperatorAddReq req) {
        log.info("addMerchantOperator req={}", JSONUtil.toJsonStr(req));
        MerchantOperatorAddCmd cmd = merchantOperatorConverter.convertMerchantOperatorAddCmd(req);

        boolean added = merchantOperatorCmdService.addMerchantOperator(cmd);
        return Mono.just(Result.ok(added));
    }

    /**
     * 编辑商户操作员 商户平台
     */
    @PostMapping("/v1/updateMerchantOperator")
    public Mono<Result<Boolean>> updateMerchantOperator(@RequestBody @Validated MerchantOperatorUpdateReq req) {
        log.info("updateMerchantOperator req={}", JSONUtil.toJsonStr(req));
        MerchantOperatorUpdateCmd cmd = merchantOperatorConverter.convertMerchantOperatorUpdateCmd(req);

        boolean updated = merchantOperatorCmdService.updateMerchantOperator(cmd);
        return Mono.just(Result.ok(updated));
    }


}
