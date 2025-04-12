package api.sphere.controller.front.admin;

import api.sphere.controller.request.MerchantAddReq;
import api.sphere.controller.request.MerchantChannelConfigListReq;
import api.sphere.controller.request.MerchantChannelConfigUpdateReq;
import api.sphere.controller.request.MerchantConfigUpdateReq;
import api.sphere.controller.request.MerchantDropListReq;
import api.sphere.controller.request.MerchantIdReq;
import api.sphere.controller.request.MerchantOperatorAddReq;
import api.sphere.controller.request.MerchantOperatorListReq;
import api.sphere.controller.request.MerchantOperatorPageReq;
import api.sphere.controller.request.MerchantOperatorUpdateReq;
import api.sphere.controller.request.MerchantPageReq;
import api.sphere.controller.request.MerchantPasswordResetReq;
import api.sphere.controller.request.MerchantUpdateReq;
import api.sphere.controller.request.MerchantVerifyReq;
import api.sphere.controller.request.PaymentLinkSettingReq;
import api.sphere.controller.request.SettleAccountListReq;
import api.sphere.controller.request.TradeCallbackReq;
import api.sphere.controller.request.UnsetGoogleCodeReq;
import api.sphere.controller.response.MerchantBaseVO;
import api.sphere.controller.response.MerchantOperatorVO;
import api.sphere.convert.MerchantChannelConfigConverter;
import api.sphere.convert.MerchantConfigConverter;
import api.sphere.convert.MerchantLoginConverter;
import api.sphere.convert.MerchantOperatorConverter;
import api.sphere.convert.MerchantConverter;
import api.sphere.convert.SettleAccountConverter;
import api.sphere.convert.TradeCallbackConverter;
import app.sphere.command.MerchantCmdService;
import app.sphere.command.MerchantChannelConfigCmdService;
import app.sphere.command.MerchantConfigCmdService;
import app.sphere.command.MerchantLoginCmdService;
import app.sphere.command.MerchantOperatorCmdService;
import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.TradeCallBackCmdService;
import app.sphere.command.cmd.MerchantAddCommand;
import app.sphere.command.cmd.MerchantChannelConfigUpdateCmd;
import app.sphere.command.cmd.MerchantConfigUpdateCmd;
import app.sphere.command.cmd.MerchantOperatorAddCmd;
import app.sphere.command.cmd.MerchantOperatorUpdateCmd;
import app.sphere.command.cmd.MerchantPasswordResetCmd;
import app.sphere.command.cmd.MerchantUnsetGoogleCodeCmd;
import app.sphere.command.cmd.MerchantUpdateCommand;
import app.sphere.command.cmd.MerchantVerifyCommand;
import app.sphere.command.cmd.PaymentLinkSettingCmd;
import app.sphere.command.cmd.TradeCallbackCommand;
import app.sphere.query.MerchantChannelConfigQueryService;
import app.sphere.query.MerchantConfigQueryService;
import app.sphere.query.MerchantOperatorQueryService;
import app.sphere.query.MerchantPayoutConfigQueryService;
import app.sphere.query.MerchantQueryService;
import app.sphere.query.MerchantStatisticsQueryService;
import app.sphere.query.MerchantWithdrawConfigQueryService;
import app.sphere.query.SettleAccountQueryService;
import app.sphere.query.dto.MerchantChannelConfigListDTO;
import app.sphere.query.dto.MerchantConfigDTO;
import app.sphere.query.dto.MerchantDropDTO;
import app.sphere.query.dto.MerchantOperatorDTO;
import app.sphere.query.dto.MerchantPaymentLinkSettingDTO;
import app.sphere.query.dto.MerchantPayoutConfigDTO;
import app.sphere.query.dto.MerchantTimelyStatisticsIndexDTO;
import app.sphere.query.dto.MerchantWithdrawConfigDTO;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.SettleAccountDTO;
import app.sphere.query.param.MerchantChannelConfigListParam;
import app.sphere.query.param.MerchantDropListParam;
import app.sphere.query.param.MerchantIdParam;
import app.sphere.query.param.MerchantOperatorListParam;
import app.sphere.query.param.MerchantOperatorPageParam;
import app.sphere.query.param.MerchantPageParam;
import app.sphere.query.param.SettleAccountListParam;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantOperator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import share.sphere.enums.QuerySourceEnum;
import share.sphere.result.PageResult;
import share.sphere.result.Result;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 管理后台商户管理接口
 * 提供商户相关的管理功能，包括：
 * 1. 商户基本信息管理
 * 2. 商户配置管理
 * 3. 商户操作员管理
 * 4. 商户安全设置
 * 5. 商户统计信息
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminMerchantController {

    // ============== 服务依赖注入 ==============
    @Resource
    MerchantQueryService merchantQueryService;
    @Resource
    MerchantConverter merchantConverter;
    @Resource
    MerchantCmdService merchantCmdService;
    @Resource
    MerchantConfigQueryService merchantConfigQueryService;
    @Resource
    MerchantConfigCmdService merchantConfigCmdService;
    @Resource
    MerchantConfigConverter merchantConfigConverter;
    @Resource
    MerchantChannelConfigQueryService merchantChannelConfigQueryService;
    @Resource
    MerchantChannelConfigCmdService merchantChannelConfigCmdService;
    @Resource
    MerchantChannelConfigConverter merchantChannelConfigConverter;
    @Resource
    MerchantLoginCmdService merchantLoginCmdService;
    @Resource
    MerchantLoginConverter merchantLoginConverter;
    @Resource
    MerchantOperatorQueryService merchantOperatorQueryService;
    @Resource
    MerchantOperatorCmdService merchantOperatorCmdService;
    @Resource
    MerchantOperatorConverter merchantOperatorConverter;
    @Resource
    MerchantPayoutConfigQueryService merchantPayoutConfigQueryService;
    @Resource
    MerchantStatisticsQueryService merchantStatisticsQueryService;
    @Resource
    MerchantWithdrawConfigQueryService merchantWithdrawConfigQueryService;


    @Resource
    SettleAccountConverter settleAccountConverter;
    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettleAccountQueryService settleAccountQueryService;

    // ============== 商户基本信息管理 ==============
    /**
     * 新增商户
     */
    @PostMapping("/v1/addMerchant")
    public Mono<Result<Boolean>> addMerchant(@RequestBody @Validated MerchantAddReq req) {
        log.info("新增商, req={}", JSONUtil.toJsonStr(req));
        MerchantAddCommand command = merchantConverter.convertMerchantAddCommand(req);
        boolean addMerchant = merchantCmdService.addMerchant(command);
        return Mono.just(Result.ok(addMerchant));
    }

    /**
     * 新增商户
     */
    @PostMapping("/v1/verifyMerchant")
    public Mono<Result<Boolean>> verifyMerchant(@RequestBody @Validated MerchantVerifyReq req) {
        log.info("新增商, req={}", JSONUtil.toJsonStr(req));
        MerchantVerifyCommand command = merchantConverter.convertMerchantVerifyCommand(req);
        boolean addMerchant = merchantCmdService.verifyMerchant(command);
        return Mono.just(Result.ok(addMerchant));
    }

    /**
     * 更新商户
     * 可用于启用/禁用商户
     */
    @PostMapping("/v1/updateMerchant")
    public Mono<Result<Boolean>> updateMerchant(@RequestBody @Validated MerchantUpdateReq req) {
        log.info("更新商户状态, merchantId={}, status={}", req.getMerchantId(), req.getStatus());
        req.setQuerySource(QuerySourceEnum.ADMIN.getCode());
        MerchantUpdateCommand command = merchantConverter.convertMerchantUpdateStatusCommand(req);
        boolean updated = merchantCmdService.updateMerchant(command);
        return Mono.just(Result.ok(updated));
    }

    /**
     * 分页查询商户列表
     * 支持按商户ID、名称、类型、状态等条件筛选
     */
    @PostMapping("/v1/pageBaseMerchantList")
    public Mono<PageResult<MerchantBaseVO>> pageBaseMerchantList(@RequestBody @Validated MerchantPageReq req) {
        log.info("分页查询商户列表, req={}", JSONUtil.toJsonStr(req));
        MerchantPageParam param = merchantConverter.convertMerchantPageParam(req);

        Page<Merchant> page = merchantQueryService.pageBaseMerchantList(param);
        List<MerchantBaseVO> voList = merchantConverter.convertMerchantBaseVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));

    }

    /**
     * 获取商户详情
     */
    @PostMapping("/v1/getMerchant")
    public Mono<Result<MerchantBaseVO>> getBaseMerchant(@RequestBody @Validated MerchantIdReq req) {
        log.info("获取商户详情, merchantId={}", req.getMerchantId());
        MerchantIdParam param = new MerchantIdParam();
        param.setMerchantId(req.getMerchantId());

        Merchant merchant = merchantQueryService.getMerchant(param);
        MerchantBaseVO vo = merchantConverter.convertMerchantBaseVO(merchant);
        return Mono.just(Result.ok(vo));
    }

    /**
     * 获取商户下拉列表
     * 用于选择框等场景
     */
    @PostMapping("/v1/dropMerchantList")
    public Mono<Result<List<MerchantDropDTO>>> dropMerchantList(@RequestBody MerchantDropListReq req) {
        log.info("获取商户下拉列表, req={}", JSONUtil.toJsonStr(req));
        MerchantDropListParam param = merchantConverter.convertMerchantDropListParam(req);
        List<MerchantDropDTO> list = merchantQueryService.dropMerchantList(param);
        return Mono.just(Result.ok(list));
    }

    // ============== 商户配置管理 ==============

    /**
     * 获取商户配置信息
     */
    @PostMapping("/v1/getMerchantConfig")
    public Mono<Result<MerchantConfigDTO>> getMerchantConfig(@RequestBody @Validated MerchantIdReq req) {
        log.info("获取商户配置信息, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.ADMIN.getCode());
        MerchantIdParam param = merchantConfigConverter.convertMerchantIdParam(req);
        MerchantConfigDTO merchantConfigDTO = merchantConfigQueryService.getMerchantConfig(param);
        return Mono.just(Result.ok(merchantConfigDTO));
    }

    /**
     * 更新商户配置信息
     */
    @PostMapping("/v1/updateMerchantConfig")
    public Mono<Result<Boolean>> updateMerchantConfig(@RequestBody @Validated MerchantConfigUpdateReq req) {
        log.info("更新商户配置信息, merchantId={}, config={}", req.getMerchantId(), JSONUtil.toJsonStr(req));
        req.setQuerySource(QuerySourceEnum.ADMIN.getCode());
        MerchantConfigUpdateCmd cmd = merchantConfigConverter.convertMerchantConfigUpdateCmd(req);
        boolean updated = merchantConfigCmdService.updateMerchantConfig(cmd);
        return Mono.just(Result.ok(updated));
    }

    /**
     * 获取商户渠道配置列表
     */
    @PostMapping("/v1/getMerchantChannelConfigList")
    public Mono<Result<MerchantChannelConfigListDTO>> getMerchantChannelConfigList(@RequestBody @Validated MerchantChannelConfigListReq req) {
        log.info("获取商户渠道配置列表, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.ADMIN.getCode());
        MerchantChannelConfigListParam param = merchantChannelConfigConverter.convertMerchantChannelConfigListParam(req);
        MerchantChannelConfigListDTO configList = merchantChannelConfigQueryService.getMerchantChannelConfigList(param);
        return Mono.just(Result.ok(configList));
    }

    /**
     * 更新商户渠道配置
     */
    @PostMapping("/v1/updateMerchantChannel")
    public Mono<Result<Boolean>> updateMerchantChannel(@RequestBody @Validated MerchantChannelConfigUpdateReq req) {
        log.info("更新商户渠道配置, merchantId={}, channel={}", req.getMerchantId(), req.getChannelCode());
        MerchantChannelConfigUpdateCmd cmd = merchantChannelConfigConverter.convertMerchantChannelConfigUpdateCmd(req);
        boolean updated = merchantChannelConfigCmdService.updateMerchantChannel(cmd);
        return Mono.just(Result.ok(updated));
    }

    /**
     * 获取支付链接配置
     */
    @PostMapping("/v1/getPaymentLinkSetting")
    public Mono<Result<MerchantPaymentLinkSettingDTO>> getPaymentLinkSetting(@RequestBody @Validated MerchantIdReq req) {
        log.info("获取支付链接配置, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.ADMIN.getCode());
        MerchantIdParam param = merchantConfigConverter.convertMerchantIdParam(req);
        MerchantPaymentLinkSettingDTO paymentLinkSetting = merchantConfigQueryService.getPaymentLinkSetting(param);
        return Mono.just(Result.ok(paymentLinkSetting));
    }

    /**
     * 更新支付链接配置
     */
    @PostMapping("/v1/updatePaymentLinkSetting")
    public Mono<Result<Boolean>> updatePaymentLinkSetting(@RequestBody @Validated PaymentLinkSettingReq req) {
        log.info("更新支付链接配置, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.ADMIN.getCode());
        PaymentLinkSettingCmd cmd = merchantConfigConverter.convertPaymentLinkSettingCmd(req);
        boolean updated = merchantConfigCmdService.updatePaymentLinkSetting(cmd);
        return Mono.just(Result.ok(updated));
    }

    /**
     * 获取商户代付配置
     */
    @PostMapping("/v1/getMerchantPayoutConfig")
    public Mono<Result<MerchantPayoutConfigDTO>> getMerchantPayoutConfig(@RequestBody @Validated MerchantIdReq req) {
        log.info("获取商户代付配置, merchantId={}", req.getMerchantId());
        MerchantIdParam param = new MerchantIdParam();
        param.setMerchantId(req.getMerchantId());
        MerchantPayoutConfigDTO payoutConfig = merchantPayoutConfigQueryService.getMerchantPayoutConfig(param);
        return Mono.just(Result.ok(payoutConfig));
    }

    /**
     * 获取商户提现配置
     */
    @PostMapping("/v1/getMerchantWithdrawConfig")
    public Mono<Result<MerchantWithdrawConfigDTO>> getMerchantWithdrawConfig(@RequestBody @Validated MerchantIdReq req) {
        log.info("获取商户提现配置, merchantId={}", req.getMerchantId());
        MerchantIdParam param = new MerchantIdParam();
        param.setMerchantId(req.getMerchantId());
        MerchantWithdrawConfigDTO withdrawConfig = merchantWithdrawConfigQueryService.getMerchantWithdrawConfig(param);
        return Mono.just(Result.ok(withdrawConfig));
    }

    // ============== 商户操作员管理 ==============

    /**
     * 分页查询商户操作员列表
     */
    @PostMapping("/v1/pageMerchantOperatorList")
    public Mono<PageResult<MerchantOperatorDTO>> pageMerchantOperatorList(@RequestBody @Validated MerchantOperatorPageReq req) {
        log.info("分页查询商户操作员列表, merchantId={}", req.getMerchantId());
        MerchantOperatorPageParam param = merchantOperatorConverter.convertMerchantOperatorPageParam(req);
        PageDTO<MerchantOperatorDTO> pageDTO = merchantOperatorQueryService.pageMerchantOperatorList(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    /**
     * 获取商户操作员列表
     */
    @PostMapping("/v1/getMerchantOperatorList")
    public Mono<Result<List<MerchantOperatorVO>>> getMerchantOperatorList(@RequestBody @Validated MerchantOperatorListReq req) {
        log.info("获取商户操作员列表, merchantId={}", req.getMerchantId());
        MerchantOperatorListParam param = merchantOperatorConverter.convertMerchantOperatorListParam(req);
        List<MerchantOperator> operatorList = merchantOperatorQueryService.getMerchantOperatorList(param);
        List<MerchantOperatorVO> voList = merchantOperatorConverter.convertMerchantOperatorList(operatorList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 新增商户操作员
     */
    @PostMapping("/v1/addMerchantOperator")
    public Mono<Result<Boolean>> addMerchantOperator(@RequestBody @Validated MerchantOperatorAddReq req) {
        log.info("新增商户操作员, merchantId={}, username={}", req.getMerchantId(), req.getUsername());
        MerchantOperatorAddCmd cmd = merchantOperatorConverter.convertMerchantOperatorAddCmd(req);
        boolean added = merchantOperatorCmdService.addMerchantOperator(cmd);
        return Mono.just(Result.ok(added));
    }

    /**
     * 更新商户操作员信息
     */
    @PostMapping("/v1/updateMerchantOperator")
    public Mono<Result<Boolean>> updateMerchantOperator(@RequestBody @Validated MerchantOperatorUpdateReq req) {
        log.info("更新商户操作员信息, req={}", JSONUtil.toJsonStr(req));
        MerchantOperatorUpdateCmd cmd = merchantOperatorConverter.convertMerchantOperatorUpdateCmd(req);
        boolean updated = merchantOperatorCmdService.updateMerchantOperator(cmd);
        return Mono.just(Result.ok(updated));
    }

    // ============== 商户安全设置 ==============

    /**
     * 重置商户密码
     */
    @PostMapping("/v1/resetPassword")
    public Mono<Result<Boolean>> resetPassword(@RequestBody @Validated MerchantPasswordResetReq req) {
        log.info("重置商户密码, req={}", JSONUtil.toJsonStr(req));
        MerchantPasswordResetCmd cmd = merchantLoginConverter.convertMerchantPasswordResetCmd(req);
        boolean reset = merchantLoginCmdService.resetPassword(cmd);
        return Mono.just(Result.ok(reset));
    }

    /**
     * 解绑谷歌验证器
     */
    @PostMapping("/v1/unsetGoogleCode")
    public Mono<Result<Boolean>> unsetGoogleCode(@RequestBody @Validated UnsetGoogleCodeReq req) {
        log.info("解绑谷歌验证器, merchantId={}", req.getMerchantId());
        MerchantUnsetGoogleCodeCmd cmd = merchantLoginConverter.convertMerchantUnsetGoogleCodeCmd(req);
        boolean unset = merchantLoginCmdService.unsetGoogleAuth(cmd);
        return Mono.just(Result.ok(unset));
    }

    // ============== 商户统计信息 ==============

    /**
     * 获取商户实时统计信息
     */
    @PostMapping("/v1/getMerchantTimelyStatistics4Index")
    public Mono<Result<MerchantTimelyStatisticsIndexDTO>> getMerchantTimelyStatistics4Index() {
        log.info("获取商户实时统计信息");
        MerchantTimelyStatisticsIndexDTO statistics = merchantStatisticsQueryService.getMerchantTimelyStatistics4Index();
        return Mono.just(Result.ok(statistics));
    }


    /**
     * 通过某条件 查询账户信息
     */
    @PostMapping("/v1/getSettleAccountList")
    public Mono<Result<List<SettleAccountDTO>>> getSettleAccountList(@RequestBody @Validated SettleAccountListReq req) {
        log.info("getAccountByCondition req={}", JSONUtil.toJsonStr(req));
        SettleAccountListParam param = settleAccountConverter.convertSettleAccountListParam(req);

        List<SettleAccountDTO> accountDTOList = settleAccountQueryService.getSettleAccountList(param);
        return Mono.just(Result.ok(accountDTOList));
    }

    @Resource
    TradeCallBackCmdService tradeCallBackCmdService;
    @javax.annotation.Resource
    TradeCallbackConverter tradeCallbackConverter;

    /**
     * 订单补发回调通知  admin
     */
    @PostMapping("/v1/callback")
    public Callable<Result<Boolean>> callback(@RequestBody @Validated TradeCallbackReq req) {
        log.info("tradeCallback req={}", JSONUtil.toJsonStr(req));
        TradeCallbackCommand cmd = tradeCallbackConverter.convertTradeCallbackCommand(req);
        return () -> Result.ok(tradeCallBackCmdService.handlerTradeCallback(cmd));
    }
}
