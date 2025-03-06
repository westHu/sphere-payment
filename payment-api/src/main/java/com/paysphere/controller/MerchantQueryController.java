package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.controller.request.MerchantDropListReq;
import com.paysphere.controller.request.MerchantIdReq;
import com.paysphere.controller.request.MerchantPageReq;
import com.paysphere.controller.response.MerchantBaseVO;
import com.paysphere.convert.MerchantQueryConverter;
import com.paysphere.db.entity.Merchant;
import com.paysphere.query.MerchantQueryService;
import com.paysphere.query.dto.MerchantDropDTO;
import com.paysphere.query.param.MerchantDropListParam;
import com.paysphere.query.param.MerchantIdParam;
import com.paysphere.query.param.MerchantPageParam;
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
 * 商户查询API
 *
 * @author West.Hu
 */
@Slf4j
@RestController
public class MerchantQueryController {

    @Resource
    MerchantQueryConverter merchantQueryConverter;
    @Resource
    MerchantQueryService merchantQueryService;


    /**
     * 商户下拉框
     */
    @PostMapping("/v1/dropMerchantList")
    public Mono<Result<List<MerchantDropDTO>>> dropMerchantList(@RequestBody MerchantDropListReq req) {
        log.info("dropMerchantList req={}", JSONUtil.toJsonStr(req));

        MerchantDropListParam param = merchantQueryConverter.convertMerchantDropListParam(req);
        List<MerchantDropDTO> merchantList = merchantQueryService.dropMerchantList(param);
        return Mono.just(Result.ok(merchantList));
    }

    /**
     * 分页查询商户基本信息列表
     */
    @PostMapping("/v1/pageBaseMerchantList")
    public Mono<PageResult<MerchantBaseVO>> pageBaseMerchantList(@RequestBody @Validated MerchantPageReq req) {
        log.info("pageBaseMerchantList req={}", JSONUtil.toJsonStr(req));
        MerchantPageParam param = merchantQueryConverter.convertMerchantPageParam(req);

        Page<Merchant> page = merchantQueryService.pageBaseMerchantList(param);
        List<MerchantBaseVO> voList = merchantQueryConverter.convertMerchantBaseVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 商户基本信息
     */
    @PostMapping("/v1/getBaseMerchant")
    public Mono<Result<MerchantBaseVO>> getBaseMerchant(@RequestBody @Validated MerchantIdReq req) {
        log.info("getBaseMerchant req={}", JSONUtil.toJsonStr(req));
        MerchantIdParam param = new MerchantIdParam();
        param.setMerchantId(req.getMerchantId());

        Merchant merchant = merchantQueryService.getBaseMerchant(param);
        MerchantBaseVO vo = merchantQueryConverter.convertMerchantBaseVO(merchant);
        return Mono.just(Result.ok(vo));
    }

}
