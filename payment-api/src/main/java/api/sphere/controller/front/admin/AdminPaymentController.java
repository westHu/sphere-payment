package api.sphere.controller.front.admin;

import api.sphere.controller.request.*;
import api.sphere.controller.response.*;
import api.sphere.convert.*;
import app.sphere.command.*;
import app.sphere.command.cmd.*;
import app.sphere.query.*;
import app.sphere.query.dto.*;
import app.sphere.query.param.*;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.PaymentMethodRepository;
import infrastructure.sphere.db.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import share.sphere.result.PageResult;
import share.sphere.result.Result;

import java.util.List;

/**
 * 管理后台支付相关接口
 * 
 * 本控制器提供支付系统管理所需的所有接口，主要包括以下功能模块：
 * 1. 支付渠道管理
 *    - 渠道基本信息管理
 *    - 渠道状态管理
 *    - 渠道配置管理
 * 2. 渠道支付方式管理
 *    - 支付方式关联管理
 *    - 支付方式费用管理
 *    - 支付方式状态管理
 * 3. 支付方式管理
 *    - 支付方式基础信息管理
 *    - 支付方式状态管理
 *    - 支付方式配置管理
 * 
 * 所有接口均采用POST方式，返回Mono<Result<T>>或Mono<PageResult<T>>格式
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminPaymentController {

    // ===================== 依赖注入 =====================
    
    // 支付渠道相关服务
    @Resource
    PaymentChannelConverter paymentChannelConverter;
    @Resource
    PaymentChannelQueryService paymentChannelQueryService;
    @Resource
    PaymentChannelCmdService paymentChannelCmdService;

    // 渠道支付方式相关服务
    @Resource
    PaymentChannelMethodConverter paymentChannelMethodConverter;
    @Resource
    PaymentChannelMethodQueryService paymentChannelMethodQueryService;
    @Resource
    PaymentChannelMethodCmdService paymentChannelMethodCmdService;

    // 支付方式相关服务
    @Resource
    PaymentMethodConverter paymentMethodConverter;
    @Resource
    PaymentMethodQueryService paymentMethodQueryService;
    @Resource
    PaymentMethodRepository paymentMethodRepository;
    @Resource
    PaymentMethodCmdService paymentMethodCmdService;

    // ===================== 支付渠道管理接口 =====================

    /**
     * 获取支付渠道下拉列表
     * 
     * @param req 支付渠道查询请求
     * @return 支付渠道下拉列表
     */
    @PostMapping(value = "/v1/dropPaymentChannelList")
    public Mono<Result<List<PaymentChannelDropDTO>>> dropPaymentChannelList(@RequestBody PaymentChannelDropReq req) {
        log.info("获取支付渠道下拉列表, req={}", JSONUtil.toJsonStr(req));
        PaymentChannelDropParam dropParam = new PaymentChannelDropParam();
        return Mono.just(Result.ok(null));
    }

    /**
     * 分页查询支付渠道列表
     * 
     * @param req 支付渠道分页查询请求
     * @return 支付渠道分页列表
     */
    @PostMapping(value = "/v1/pagePaymentChannelList")
    public Mono<PageResult<PaymentChannelVO>> pagePaymentChannelList(@RequestBody @Validated PaymentChannelPageReq req) {
        log.info("分页查询支付渠道列表, req={}", JSONUtil.toJsonStr(req));
        PaymentChannelPageParam param = paymentChannelConverter.convertPaymentChannelPageParam(req);

        Page<PaymentChannel> page = paymentChannelQueryService.pagePaymentChannelList(param);
        List<PaymentChannelVO> voList = paymentChannelConverter.convertPaymentChannelVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 获取支付渠道列表
     * 
     * @param req 支付渠道查询请求
     * @return 支付渠道列表
     */
    @PostMapping(value = "/v1/getPaymentChannelList")
    public Mono<Result<List<PaymentChannelVO>>> getPaymentChannelList(@RequestBody @Validated PaymentChannelListReq req) {
        log.info("获取支付渠道列表, req={}", JSONUtil.toJsonStr(req));
        PaymentChannelListParam param = paymentChannelConverter.convertPaymentChannelListParam(req);

        List<PaymentChannel> channelList = paymentChannelQueryService.getPaymentChannelList(param);
        List<PaymentChannelVO> voList = paymentChannelConverter.convertPaymentChannelVOList(channelList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 开启或关闭支付渠道
     * 
     * @param req 支付渠道状态变更请求
     * @return 操作结果
     */
    @PostMapping(value = "/v1/openOrClosePaymentChannel")
    public Mono<Result<Boolean>> openOrClosePaymentChannel(@RequestBody @Validated PaymentChannelStatusReq req) {
        log.info("开启或关闭支付渠道, id={}, status={}", req.getId(), req.getStatus());
        PaymentChannelStatusCommand command = paymentChannelConverter.convertPaymentChannelStatusCommand(req);

        return Mono.just(Result.ok(paymentChannelCmdService.openOrClosePaymentChannel(command)));
    }

    /**
     * 更新支付渠道信息
     * 
     * @param req 支付渠道更新请求
     * @return 更新结果
     */
    @PostMapping(value = "/v1/updatePaymentChannel")
    public Mono<Result<Boolean>> updatePaymentChannel(@RequestBody @Validated PaymentChannelUpdateReq req) {
        log.info("更新支付渠道信息, id={}", req.getId());
        PaymentChannelUpdateCommand command = paymentChannelConverter.convertPaymentChannelUpdateCommand(req);

        return Mono.just(Result.ok(paymentChannelCmdService.updatePaymentChannel(command)));
    }

    // ===================== 渠道支付方式管理接口 =====================

    /**
     * 按支付方式分组查询渠道支付方式
     * 
     * @param req 分组查询请求
     * @return 分组后的渠道支付方式列表
     */
    @PostMapping(value = "/v1/groupPaymentChannelMethodList")
    public Mono<Result<List<PaymentChannelMethodGroupDTO>>> groupPaymentChannelMethodList(@RequestBody @Validated PaymentChannelMethodGroupReq req) {
        log.info("按支付方式分组查询渠道支付方式, req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodGroupParam param = paymentChannelMethodConverter.convertPaymentChannelMethodGroupParam(req);
        List<PaymentChannelMethodGroupDTO> dtoList = paymentChannelMethodQueryService.groupPaymentChannelMethodList(param);
        return Mono.just(Result.ok(dtoList));
    }

    /**
     * 按渠道分组查询支付方式
     * 
     * @param req 分组查询请求
     * @return 分组后的支付方式列表
     */
    @PostMapping(value = "/v1/groupChannelPaymentMethodList")
    public Mono<Result<List<ChannelPaymentMethodGroupDTO>>> groupChannelPaymentMethodList(@RequestBody @Validated PaymentChannelMethodGroupReq req) {
        log.info("按渠道分组查询支付方式, req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodGroupParam param = paymentChannelMethodConverter.convertPaymentChannelMethodGroupParam(req);
        List<ChannelPaymentMethodGroupDTO> dtoList = paymentChannelMethodQueryService.groupChannelPaymentMethodList(param);
        return Mono.just(Result.ok(dtoList));
    }

    /**
     * 分页查询渠道支付方式
     * 
     * @param req 分页查询请求
     * @return 渠道支付方式分页列表
     */
    @PostMapping(value = "/v1/pagePaymentChannelMethodList")
    public Mono<PageResult<PaymentChannelMethodVO>> pagePaymentChannelMethodList(@RequestBody @Validated PaymentChannelMethodPageReq req) {
        log.info("分页查询渠道支付方式, req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodPageParam param = paymentChannelMethodConverter.convertPaymentChannelMethodPageParam(req);
        Page<PaymentChannelMethod> page = paymentChannelMethodQueryService.pagePaymentChannelMethodList(param);
        List<PaymentChannelMethodVO> voList = paymentChannelMethodConverter.convertPaymentChannelMethodVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 获取渠道支付方式详情
     * 
     * @param req 查询请求
     * @return 渠道支付方式详情
     */
    @PostMapping(value = "/v1/getPaymentChannelMethod")
    public Mono<Result<PaymentChannelMethodVO>> getPaymentChannelMethod(@RequestBody @Validated PaymentChannelMethodReq req) {
        log.info("获取渠道支付方式详情, req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodParam param = paymentChannelMethodConverter.convertPaymentChannelMethodParam(req);
        PaymentChannelMethod method = paymentChannelMethodQueryService.getPaymentChannelMethod(param);
        PaymentChannelMethodVO vo = paymentChannelMethodConverter.convertPaymentChannelMethodVO(method);
        return Mono.just(Result.ok(vo));
    }

    /**
     * 获取渠道支付方式费用范围
     * 
     * @param req 查询请求
     * @return 渠道支付方式费用范围
     */
    @PostMapping(value = "/v1/getPaymentChannelMethodFeeRange")
    public Mono<Result<PaymentChannelMethodFeeRangeDTO>> getPaymentChannelMethodFeeRange(@RequestBody @Validated PaymentChannelMethodRangeReq req) {
        log.info("获取渠道支付方式费用范围, req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodRangeParam param = paymentChannelMethodConverter.convertPaymentChannelMethodRangeParam(req);
        PaymentChannelMethodFeeRangeDTO dto = paymentChannelMethodQueryService.getPaymentChannelMethodFeeRange(param);
        return Mono.just(Result.ok(dto));
    }

    /**
     * 添加渠道支付方式
     * 
     * @param req 添加请求
     * @return 添加结果
     */
    @PostMapping(value = "/v1/addPaymentChannelMethod")
    public Mono<Result<Boolean>> addPaymentChannelMethod(@RequestBody @Validated PaymentChannelMethodAddReq req) {
        log.info("添加渠道支付方式, req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodAddCommand command = paymentChannelMethodConverter.convertPaymentChannelMethodAddCommand(req);
        return Mono.just(Result.ok(paymentChannelMethodCmdService.addPaymentChannelMethod(command)));
    }

    /**
     * 更新渠道支付方式
     * 
     * @param req 更新请求
     * @return 更新结果
     */
    @PostMapping(value = "/v1/updatePaymentChannelMethod")
    public Mono<Result<Boolean>> updatePaymentChannelMethod(@RequestBody @Validated PaymentChannelMethodUpdateReq req) {
        log.info("更新渠道支付方式, req={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodUpdateCommand command = paymentChannelMethodConverter.convertPaymentChannelMethodUpdateCommand(req);
        return Mono.just(Result.ok(paymentChannelMethodCmdService.updatePaymentChannelMethod(command)));
    }

    /**
     * 开启或关闭渠道支付方式
     * 
     * @param reqList 状态变更请求列表
     * @return 操作结果
     */
    @PostMapping(value = "/v1/openOrClosePaymentChannelMethod")
    public Mono<Result<Boolean>> openOrClosePaymentChannelMethod(@RequestBody @Validated PaymentChannelMethodStatusReq req) {
        log.info("开启或关闭渠道支付方式, reqList={}", JSONUtil.toJsonStr(req));
        PaymentChannelMethodStatusCommand command = paymentChannelMethodConverter.convertPaymentChannelMethodStatusCommand(req);
        return Mono.just(Result.ok(paymentChannelMethodCmdService.openOrClosePaymentChannelMethod(command)));
    }

    // ===================== 支付方式管理接口 =====================

    /**
     * 获取支付方式下拉列表
     * 
     * @param req 查询请求
     * @return 支付方式下拉列表
     */
    @PostMapping(value = "/v1/dropPaymentMethodList")
    public Mono<Result<List<PaymentMethodDropVO>>> dropPaymentMethodList(@RequestBody PaymentMethodDropReq req) {
        log.info("获取支付方式下拉列表, req={}", JSONUtil.toJsonStr(req));
        List<PaymentMethod> methodList = paymentMethodRepository.list();
        List<PaymentMethodDropVO> voList = paymentMethodConverter.convertPaymentMethodDropVOList(methodList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 分页查询支付方式列表
     * 
     * @param req 分页查询请求
     * @return 支付方式分页列表
     */
    @PostMapping(value = "/v1/pagePaymentMethodList")
    public Mono<PageResult<PaymentMethodVO>> pagePaymentMethodList(@RequestBody @Validated PaymentMethodPageReq req) {
        log.info("分页查询支付方式列表, req={}", JSONUtil.toJsonStr(req));
        PaymentMethodPageParam param = paymentMethodConverter.convertPaymentMethodPageParam(req);
        Page<PaymentMethod> page = paymentMethodQueryService.pagePaymentMethodList(param);
        List<PaymentMethodVO> voList = paymentMethodConverter.convertPaymentMethodVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 获取支付方式详情
     * 
     * @param req 查询请求
     * @return 支付方式详情
     */
    @PostMapping(value = "/v1/getPaymentMethod")
    public Mono<Result<PaymentMethodVO>> getPaymentMethod(@RequestBody @Validated PaymentMethodReq req) {
        log.info("获取支付方式详情, req={}", JSONUtil.toJsonStr(req));
        PaymentMethodParam param = paymentMethodConverter.convertPaymentMethodParam(req);
        PaymentMethod method = paymentMethodQueryService.getPaymentMethod(param);
        PaymentMethodVO vo = paymentMethodConverter.convertPaymentMethodVO(method);
        return Mono.just(Result.ok(vo));
    }

    /**
     * 获取代付支付方式列表
     * 
     * @return 代付支付方式列表
     */
    @PostMapping(value = "/v1/getPaymentMethodList4Disbursement")
    public Mono<Result<List<PaymentMethodVO>>> getPaymentMethodList4Disbursement() {
        log.info("获取代付支付方式列表");
        List<PaymentMethod> methodList = paymentMethodQueryService.getPaymentMethodList4Disbursement();
        List<PaymentMethodVO> voList = paymentMethodConverter.convertPaymentMethodVOList(methodList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 获取交易支付方式列表
     * 
     * @return 交易支付方式列表
     */
    @PostMapping(value = "/v1/getPaymentMethodList4Transaction")
    public Mono<Result<List<PaymentMethodVO>>> getPaymentMethodList4Transaction() {
        log.info("获取交易支付方式列表");
        List<PaymentMethod> methodList = paymentMethodQueryService.getPaymentMethodList4Transaction();
        List<PaymentMethodVO> voList = paymentMethodConverter.convertPaymentMethodVOList(methodList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 开启或关闭支付方式
     *
     * @param req 状态变更请求
     * @return 操作结果
     */
    @PostMapping(value = "/v1/openOrClosePaymentMethod")
    public Mono<Result<Boolean>> openOrClosePaymentMethod(@RequestBody @Validated PaymentMethodStatusReq req) {
        log.info("开启或关闭支付方式, id={}, status={}", req.getId(), req.getStatus());
        PaymentMethodStatusCommand command = paymentMethodConverter.convertPaymentMethodStatusCommand(req);
        return Mono.just(Result.ok(paymentMethodCmdService.openOrClosePaymentMethod(command)));
    }

    /**
     * 更新支付方式信息
     * 
     * @param req 更新请求
     * @return 更新结果
     */
    @PostMapping(value = "/v1/updatePaymentMethod")
    public Mono<Result<Boolean>> updatePaymentMethod(@RequestBody @Validated PaymentMethodUpdateReq req) {
        log.info("更新支付方式信息, id={}", req.getId());
        PaymentMethodUpdateCommand command = paymentMethodConverter.convertPaymentMethodUpdateCommand(req);
        return Mono.just(Result.ok(paymentMethodCmdService.updatePaymentMethod(command)));
    }
}
