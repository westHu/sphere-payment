package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.paysphere.command.PaymentMethodCmdService;
import com.paysphere.command.cmd.PaymentMethodStatusCommand;
import com.paysphere.command.cmd.PaymentMethodUpdateCommand;
import com.paysphere.controller.request.PaymentMethodPageReq;
import com.paysphere.controller.request.PaymentMethodReq;
import com.paysphere.controller.request.PaymentMethodStatusReq;
import com.paysphere.controller.request.PaymentMethodUpdateReq;
import com.paysphere.controller.response.PaymentMethodDropVO;
import com.paysphere.controller.response.PaymentMethodVO;
import com.paysphere.convert.PaymentMethodConverter;
import com.paysphere.db.entity.PaymentMethod;
import com.paysphere.query.PaymentMethodQueryService;
import com.paysphere.query.param.PaymentMethodPageParam;
import com.paysphere.query.param.PaymentMethodParam;
import com.paysphere.repository.PaymentMethodService;
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
public class PaymentMethodController {

    @Resource
    PaymentMethodConverter paymentMethodConverter;
    @Resource
    PaymentMethodQueryService paymentMethodQueryService;
    @Resource
    PaymentMethodService paymentMethodService;
    @Resource
    PaymentMethodCmdService paymentMethodCmdService;


    /**
     * 支付方式下拉框
     */
    @PostMapping(value = "/v1/dropPaymentMethodList")
    public Mono<Result<List<PaymentMethodDropVO>>> dropPaymentMethodList() {
        log.info("dropPaymentMethodList");

        List<PaymentMethod> methodList = paymentMethodService.list();
        List<PaymentMethodDropVO> voList = paymentMethodConverter.convertPaymentMethodDropVOList(methodList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 分页查询支付方式
     */
    @PostMapping(value = "/v1/pagePaymentMethodList")
    public Mono<PageResult<PaymentMethodVO>> pagePaymentMethodList(@RequestBody @Validated PaymentMethodPageReq req) {
        log.info("pagePaymentMethodList req={}", JSONUtil.toJsonStr(req));
        PaymentMethodPageParam param = paymentMethodConverter.convertPaymentMethodPageParam(req);

        Page<PaymentMethod> page = paymentMethodQueryService.pagePaymentMethodList(param);
        List<PaymentMethodVO> voList = paymentMethodConverter.convertPaymentMethodVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 查询支付方式
     */
    @PostMapping(value = "/v1/getPaymentMethod")
    public Mono<Result<PaymentMethodVO>> getPaymentMethod(@RequestBody @Validated PaymentMethodReq req) {
        log.info("getPaymentMethod req={}", JSONUtil.toJsonStr(req));
        PaymentMethodParam param = paymentMethodConverter.convertPaymentMethodParam(req);

        PaymentMethod method = paymentMethodQueryService.getPaymentMethod(param);
        PaymentMethodVO vo = paymentMethodConverter.convertPaymentMethodVO(method);
        return Mono.just(Result.ok(vo));
    }


    /**
     * 查询出款可用支付方式（提现） 即可用支付方式, 谨慎使用
     */
    @PostMapping(value = "/v1/getPaymentMethodList4Disbursement")
    public Mono<Result<List<PaymentMethodVO>>> getPaymentMethodList4Disbursement() {

        List<PaymentMethod> methodList = paymentMethodQueryService.getPaymentMethodList4Disbursement();
        List<PaymentMethodVO> voList = paymentMethodConverter.convertPaymentMethodVOList(methodList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 查询收款支付方式（收银台） 即可用支付方式, 谨慎使用
     */
    @PostMapping(value = "/v1/getPaymentMethodList4Transaction")
    public Mono<Result<List<PaymentMethodVO>>> getPaymentMethodList4Transaction() {
        log.info("getPaymentMethodList4Transaction");
        List<PaymentMethod> methodList = paymentMethodQueryService.getPaymentMethodList4Transaction();
        List<PaymentMethodVO> voList = paymentMethodConverter.convertPaymentMethodVOList(methodList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 开关支付方式
     */
    @PostMapping(value = "/v1/openOrClosePaymentMethod")
    public Mono<Result<Boolean>> openOrClosePaymentMethod(@RequestBody @Validated PaymentMethodStatusReq req) {
        log.info("openOrClosePaymentMethod req={}", JSONUtil.toJsonStr(req));
        PaymentMethodStatusCommand command = paymentMethodConverter.convertPaymentMethodStatusCommand(req);

        boolean openOrClosePaymentMethod = paymentMethodCmdService.openOrClosePaymentMethod(command);
        return Mono.just(Result.ok(openOrClosePaymentMethod));
    }

    /**
     * 更新支付方式
     */
    @PostMapping(value = "/v1/updatePaymentMethod")
    public Mono<Result<Boolean>> updatePaymentMethod(@RequestBody @Validated PaymentMethodUpdateReq req) {
        log.info("UpdatePaymentMethod req={}", JSONUtil.toJsonStr(req));
        PaymentMethodUpdateCommand command = paymentMethodConverter.convertMethodUpdateCommand(req);

        boolean updatePaymentMethod = paymentMethodCmdService.updatePaymentMethod(command);
        return Mono.just(Result.ok(updatePaymentMethod));
    }
}
