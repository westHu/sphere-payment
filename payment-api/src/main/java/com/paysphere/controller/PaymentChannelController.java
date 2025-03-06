package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.paysphere.command.PaymentChannelCmdService;
import com.paysphere.command.cmd.PaymentChannelStatusCommand;
import com.paysphere.command.cmd.PaymentChannelUpdateCommand;
import com.paysphere.controller.request.PaymentChannelListReq;
import com.paysphere.controller.request.PaymentChannelPageReq;
import com.paysphere.controller.request.PaymentChannelStatusReq;
import com.paysphere.controller.request.PaymentChannelUpdateReq;
import com.paysphere.controller.response.PaymentChannelVO;
import com.paysphere.convert.PaymentChannelConverter;
import com.paysphere.db.entity.PaymentChannel;
import com.paysphere.query.PaymentChannelQueryService;
import com.paysphere.query.param.PaymentChannelListParam;
import com.paysphere.query.param.PaymentChannelPageParam;
import com.paysphere.repository.PaymentChannelService;
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
public class PaymentChannelController {

    @Resource
    PaymentChannelConverter paymentChannelConverter;
    @Resource
    PaymentChannelQueryService paymentChannelQueryService;
    @Resource
    PaymentChannelService paymentChannelService;
    @Resource
    PaymentChannelCmdService paymentChannelCmdService;


    /**
     * 渠道下拉框
     */
    @PostMapping(value = "/v1/dropPaymentChannelList")
    public Mono<Result<List<String>>> dropPaymentChannelList() {
        log.info("dropPaymentChannelList");

        List<PaymentChannel> channelList = paymentChannelService.list();
//        List<PaymentChannelDropDTO> voList = paymentChannelConverter.convertPaymentChannelDropVOList(channelList);
        return Mono.just(Result.ok(null));
    }

    /**
     * 分页查询渠道
     */
    @PostMapping(value = "/v1/pagePaymentChannelList")
    public Mono<PageResult<PaymentChannelVO>> pagePaymentChannelList(@RequestBody @Validated PaymentChannelPageReq req) {
        log.info("pagePaymentChannelList req={}", JSONUtil.toJsonStr(req));
        PaymentChannelPageParam param = paymentChannelConverter.convertPaymentChannelPageParam(req);

        Page<PaymentChannel> page = paymentChannelQueryService.pagePaymentChannelList(param);
        List<PaymentChannelVO> voList = paymentChannelConverter.convertPaymentChannelVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 查询渠道列表
     */
    @PostMapping(value = "/v1/getPaymentChannelList")
    public Mono<Result<List<PaymentChannelVO>>> getPaymentChannelList(@RequestBody @Validated PaymentChannelListReq req) {
        log.info("pagePaymentChannelList req={}", JSONUtil.toJsonStr(req));
        PaymentChannelListParam param = paymentChannelConverter.convertPaymentChannelListParam(req);

        List<PaymentChannel> channelList = paymentChannelQueryService.getPaymentChannelList(param);
        List<PaymentChannelVO> voList = paymentChannelConverter.convertPaymentChannelVOList(channelList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 开启和关闭渠道
     */
    @PostMapping(value = "/v1/openOrClosePaymentChannel")
    public Mono<Result<Boolean>> openOrClosePaymentChannel(@RequestBody @Validated PaymentChannelStatusReq req) {
        log.info("openOrClosePaymentChannel req={}", JSONUtil.toJsonStr(req));
        PaymentChannelStatusCommand command = paymentChannelConverter.convertPaymentChannelStatusCommand(req);

        return Mono.just(Result.ok(paymentChannelCmdService.openOrClosePaymentChannel(command)));
    }

    /**
     * 更新渠道
     */
    @PostMapping(value = "/v1/updatePaymentChannel")
    public Mono<Result<Boolean>> updatePaymentChannel(@RequestBody @Validated PaymentChannelUpdateReq req) {
        log.info("updatePaymentChannel req={}", JSONUtil.toJsonStr(req));
        PaymentChannelUpdateCommand command = paymentChannelConverter.convertPaymentChannelUpdateCommand(req);

        return Mono.just(Result.ok(paymentChannelCmdService.updatePaymentChannel(command)));
    }

}
