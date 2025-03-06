package com.paysphere.controller;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.command.PaymentChannelMethodCmdService;
import com.paysphere.command.cmd.PaymentChannelMethodAddCommand;
import com.paysphere.command.cmd.PaymentChannelMethodStatusCommand;
import com.paysphere.command.cmd.PaymentChannelMethodUpdateCommand;
import com.paysphere.controller.request.IdReq;
import com.paysphere.controller.request.PaymentChannelMethodAddReq;
import com.paysphere.controller.request.PaymentChannelMethodGroupReq;
import com.paysphere.controller.request.PaymentChannelMethodPageReq;
import com.paysphere.controller.request.PaymentChannelMethodRangeReq;
import com.paysphere.controller.request.PaymentChannelMethodReq;
import com.paysphere.controller.request.PaymentChannelMethodStatusReq;
import com.paysphere.controller.request.PaymentChannelMethodUpdateReq;
import com.paysphere.controller.response.PaymentChannelMethodVO;
import com.paysphere.convert.PaymentChannelMethodConverter;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.query.PaymentChannelMethodQueryService;
import com.paysphere.query.dto.ChannelPaymentMethodGroupDTO;
import com.paysphere.query.dto.PaymentChannelMethodFeeRangeDTO;
import com.paysphere.query.dto.PaymentChannelMethodGroupDTO;
import com.paysphere.query.param.PaymentChannelMethodGroupParam;
import com.paysphere.query.param.PaymentChannelMethodPageParam;
import com.paysphere.query.param.PaymentChannelMethodParam;
import com.paysphere.query.param.PaymentChannelMethodRangeParam;
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
public class PaymentChannelMethodController {

    @Resource
    PaymentChannelMethodConverter paymentChannelMethodConverter;
    @Resource
    PaymentChannelMethodQueryService paymentChannelMethodQueryService;
    @Resource
    PaymentChannelMethodCmdService paymentChannelMethodCmdService;


    /**
     * 分组查询渠道支付方式 根据支付方式
     */
    @PostMapping(value = "/v1/groupPaymentChannelMethodList")
    public Mono<Result<List<PaymentChannelMethodGroupDTO>>> groupPaymentChannelMethodList(@RequestBody @Validated
                                                                                          PaymentChannelMethodGroupReq req) {
        log.info("groupPaymentChannelMethodList req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodGroupParam param = paymentChannelMethodConverter.convertPaymentChannelMethodGroupParam(req);
        List<PaymentChannelMethodGroupDTO> dtoList =
                paymentChannelMethodQueryService.groupPaymentChannelMethodList(param);
        return Mono.just(Result.ok(dtoList));
    }

    /**
     * 分组查询渠道支付方式 根据渠道
     */
    @PostMapping(value = "/v1/groupChannelPaymentMethodList")
    public Mono<Result<List<ChannelPaymentMethodGroupDTO>>> groupChannelPaymentMethodList(@RequestBody @Validated PaymentChannelMethodGroupReq req) {
        log.info("groupPaymentChannelMethodList req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodGroupParam param = paymentChannelMethodConverter.convertPaymentChannelMethodGroupParam(req);
        List<ChannelPaymentMethodGroupDTO> dtoList =
                paymentChannelMethodQueryService.groupChannelPaymentMethodList(param);
        return Mono.just(Result.ok(dtoList));
    }

    /**
     * 分页查询渠道支付方式
     */
    @PostMapping(value = "/v1/pagePaymentChannelMethodList")
    public Mono<PageResult<PaymentChannelMethodVO>> pagePaymentChannelMethodList(@RequestBody @Validated PaymentChannelMethodPageReq req) {
        log.info("pagePaymentChannelMethodList req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodPageParam param = paymentChannelMethodConverter.convertPaymentChannelMethodPageParam(req);
        Page<PaymentChannelMethod> page = paymentChannelMethodQueryService.pagePaymentChannelMethodList(param);
        List<PaymentChannelMethodVO> voList =
                paymentChannelMethodConverter.convertPaymentChannelMethodVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 查询渠道支付方式
     */
    @PostMapping(value = "/v1/getPaymentChannelMethod")
    public Mono<Result<PaymentChannelMethodVO>> getPaymentChannelMethod(@RequestBody @Validated PaymentChannelMethodReq req) {
        log.info("getPaymentChannelMethod req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodParam param = paymentChannelMethodConverter.convertPaymentChannelMethodParam(req);
        PaymentChannelMethod channelMethod = paymentChannelMethodQueryService.getPaymentChannelMethod(param);
        PaymentChannelMethodVO vo = paymentChannelMethodConverter.convertPaymentChannelMethodVO(channelMethod);
        return Mono.just(Result.ok(vo));
    }

    /**
     * 开关渠道支付方式
     */
    @PostMapping(value = "/v1/openOrClosePaymentChannelMethod")
    public Mono<Result<Boolean>> openOrClosePaymentChannelMethod(@RequestBody @Validated List<PaymentChannelMethodStatusReq> reqList) {
        log.info("payment method openOrClosePaymentMethod req={}", JSONUtil.toJsonStr(reqList));
        List<PaymentChannelMethodStatusCommand> commandList
                = paymentChannelMethodConverter.convertPaymentChannelMethodStatusCommand(reqList);

        boolean openOrClose = paymentChannelMethodCmdService.openOrClosePaymentChannelMethod(commandList);
        return Mono.just(Result.ok(openOrClose));
    }


    /**
     * 新增渠道支付方式
     */
    @PostMapping(value = "/v1/addPaymentChannelMethod")
    public Mono<Result<Boolean>> addPaymentChannelMethod(@RequestBody @Validated PaymentChannelMethodAddReq req) {
        log.info("addPaymentChannelMethod req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodAddCommand command =
                paymentChannelMethodConverter.convertPaymentChannelMethodAddCommand(req);

        boolean addPaymentChannelMethod = paymentChannelMethodCmdService.addPaymentChannelMethod(command);
        return Mono.just(Result.ok(addPaymentChannelMethod));
    }


    /**
     * 更新渠道支付方式
     */
    @PostMapping(value = "/v1/updatePaymentChannelMethod")
    public Mono<Result<Boolean>> updatePaymentChannelMethod(@RequestBody @Validated PaymentChannelMethodUpdateReq req) {
        log.info("updatePaymentChannelMethod req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodUpdateCommand command =
                paymentChannelMethodConverter.convertPaymentChannelMethodUpdateCommand(req);
        return Mono.just(Result.ok(paymentChannelMethodCmdService.updatePaymentChannelMethod(command)));
    }

    /**
     * 删除渠道支付方式
     */
    @PostMapping(value = "/v1/deletePaymentChannelMethod")
    public Mono<Result<Boolean>> deletePaymentChannelMethod(@RequestBody @Validated IdReq req) {
        log.info("deletePaymentChannelMethod req={}", JSONUtil.toJsonStr(req));
        boolean deletePaymentChannelMethod = paymentChannelMethodCmdService.deletePaymentChannelMethod(req.getId());
        return Mono.just(Result.ok(deletePaymentChannelMethod));
    }


    /**
     * 查询指定支付方式的费用范围
     */
    @PostMapping(value = "/v1/getPaymentChannelMethodFeeRange")
    public Mono<Result<PaymentChannelMethodFeeRangeDTO>> getPaymentChannelMethodFeeRange(@RequestBody @Validated PaymentChannelMethodRangeReq req) {
        log.info("getPaymentChannelMethodFeeRange req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodRangeParam param = paymentChannelMethodConverter.convertPaymentChannelMethodRangeParam(req);
        PaymentChannelMethodFeeRangeDTO range = paymentChannelMethodQueryService.getPaymentChannelMethodFeeRange(param);
        return Mono.just(Result.ok(range));
    }
}
