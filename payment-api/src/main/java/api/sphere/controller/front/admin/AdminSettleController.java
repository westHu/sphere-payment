package api.sphere.controller.front.admin;

import api.sphere.controller.request.SettleAccountAddReq;
import api.sphere.controller.request.SettleAccountAmountFrozenReq;
import api.sphere.controller.request.SettleAccountAmountUnfrozenReq;
import api.sphere.controller.request.SettleAccountDropReq;
import api.sphere.controller.request.SettleAccountFlowPageReq;
import api.sphere.controller.request.SettleAccountListReq;
import api.sphere.controller.request.SettleAccountPageReq;
import api.sphere.controller.request.SettleAccountSnapshotReq;
import api.sphere.controller.request.SettleOrderPageReq;
import api.sphere.controller.request.SettleOrderReq;
import api.sphere.controller.request.SettleRefundReq;
import api.sphere.controller.request.SettleSupplementReq;
import api.sphere.controller.request.SettleTimelyStatisticsIndexReq;
import api.sphere.controller.response.SettleAccountFlowVO;
import api.sphere.controller.response.SettleAccountVO;
import api.sphere.convert.SettleAccountConverter;
import api.sphere.convert.SettleAccountSnapshotConverter;
import api.sphere.convert.SettleConverter;
import api.sphere.convert.SettleFlowConverter;
import api.sphere.convert.SettleStatisticsConverter;
import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.SettleOrderCmdService;
import app.sphere.command.cmd.SettleAccountAddCmd;
import app.sphere.command.cmd.SettleAccountUpdateFrozenCmd;
import app.sphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import app.sphere.command.cmd.SettleRefundCmd;
import app.sphere.command.cmd.SettleSupplementCmd;
import app.sphere.query.SettleAccountQueryService;
import app.sphere.query.SettleAccountSnapshotQueryService;
import app.sphere.query.SettleFlowQueryService;
import app.sphere.query.SettleOrderQueryService;
import app.sphere.query.SettleStatisticsQueryService;
import app.sphere.query.dto.AccountSnapshotDTO;
import app.sphere.query.dto.SettleAccountDTO;
import app.sphere.query.dto.SettleAccountDropDTO;
import app.sphere.query.dto.SettleOrderDTO;
import app.sphere.query.dto.SettleTimelyStatisticsIndexDTO;
import app.sphere.query.param.SettleAccountDropParam;
import app.sphere.query.param.SettleAccountFlowPageParam;
import app.sphere.query.param.SettleAccountListParam;
import app.sphere.query.param.SettleAccountPageParam;
import app.sphere.query.param.SettleAccountSnapshotParam;
import app.sphere.query.param.SettleOrderPageParam;
import app.sphere.query.param.SettleOrderParam;
import app.sphere.query.param.SettleTimelyStatisticsIndexParam;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.SettleAccount;
import infrastructure.sphere.db.entity.SettleAccountFlow;
import infrastructure.sphere.db.entity.SettleOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import share.sphere.result.PageResult;
import share.sphere.result.Result;

import java.util.List;

/**
 * 管理后台结算相关接口
 * 
 * 本控制器提供结算系统管理所需的所有接口，主要包括以下功能模块：
 * 1. 结算账户管理
 *    - 账户基本信息管理
 *    - 账户余额管理
 *    - 账户状态管理
 * 2. 账户流水管理
 *    - 流水记录查询
 *    - 流水记录导出
 * 3. 账户快照管理
 *    - 账户余额快照
 * 4. 结算订单管理
 *    - 订单查询
 *    - 订单处理
 * 5. 结算统计管理
 *    - 实时统计
 * 
 * 所有接口均采用POST方式，返回Mono<Result<T>>或Mono<PageResult<T>>格式
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminSettleController {

    // ===================== 依赖注入 =====================
    
    // 结算账户相关服务
    @Resource
    SettleAccountConverter settleAccountConverter;
    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettleAccountQueryService settleAccountQueryService;

    // 账户流水相关服务
    @Resource
    SettleFlowConverter settleFlowConverter;
    @Resource
    SettleFlowQueryService settleFlowQueryService;

    // 账户快照相关服务
    @Resource
    SettleAccountSnapshotConverter settleAccountSnapshotConverter;
    @Resource
    SettleAccountSnapshotQueryService settleAccountSnapshotQueryService;

    // 结算订单相关服务
    @Resource
    SettleConverter settleConverter;
    @Resource
    SettleOrderQueryService settleOrderQueryService;
    @Resource
    SettleOrderCmdService settleOrderCmdService;

    // 结算统计相关服务
    @Resource
    SettleStatisticsConverter settleStatisticsConverter;
    @Resource
    SettleStatisticsQueryService settleStatisticsQueryService;

    // ===================== 结算账户管理接口 =====================

    /**
     * 获取结算账户下拉列表
     * 
     * @param req 账户查询请求
     * @return 账户下拉列表
     */
    @PostMapping("/v1/dropSettleAccountList")
    public Mono<Result<List<SettleAccountDropDTO>>> dropSettleAccountList(@RequestBody SettleAccountDropReq req) {
        log.info("获取结算账户下拉列表, req={}", JSONUtil.toJsonStr(req));
        SettleAccountDropParam param = settleAccountConverter.convertSettleAccountDropParam(req);
        List<SettleAccountDropDTO> dropDTOList = settleAccountQueryService.dropSettleAccountList(param);
        return Mono.just(Result.ok(dropDTOList));
    }

    /**
     * 分页查询结算账户列表
     * 
     * @param req 账户分页查询请求
     * @return 账户分页列表
     */
    @PostMapping("/v1/pageSettleAccountList")
    public Mono<PageResult<SettleAccountVO>> pageSettleAccountList(@RequestBody @Validated SettleAccountPageReq req) {
        log.info("分页查询结算账户列表, req={}", JSONUtil.toJsonStr(req));
        SettleAccountPageParam param = settleAccountConverter.convertAccountPageParam(req);
        Page<SettleAccount> page = settleAccountQueryService.pageSettleAccountList(param);
        List<SettleAccountVO> voList = settleAccountConverter.convertAccountVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 获取结算账户列表
     * 
     * @param req 账户查询请求
     * @return 账户列表
     */
    @PostMapping("/v1/getSettleAccountList")
    public Mono<Result<List<SettleAccountDTO>>> getSettleAccountList(@RequestBody @Validated SettleAccountListReq req) {
        log.info("获取结算账户列表, req={}", JSONUtil.toJsonStr(req));
        SettleAccountListParam param = settleAccountConverter.convertSettleAccountListParam(req);
        List<SettleAccountDTO> accountDTOList = settleAccountQueryService.getSettleAccountList(param);
        return Mono.just(Result.ok(accountDTOList));
    }

    /**
     * 新增结算账户
     * 
     * @param req 账户新增请求
     * @return 新增结果
     */
    @PostMapping("/v1/addSettleAccount")
    public Mono<Result<Boolean>> addSettleAccount(@RequestBody @Validated SettleAccountAddReq req) {
        log.info("新增结算账户, req={}", JSONUtil.toJsonStr(req));
        SettleAccountAddCmd cmd = settleAccountConverter.convertSettleAccountAddCmd(req);
        return Mono.just(Result.ok(settleAccountCmdService.addSettleAccount(cmd)));
    }

    /**
     * 冻结账户金额
     * 
     * @param req 金额冻结请求
     * @return 冻结结果
     */
    @PostMapping("/v1/frozenAmount")
    public Mono<Result<Boolean>> frozenAmount(@RequestBody @Validated SettleAccountAmountFrozenReq req) {
        log.info("冻结账户金额, req={}", JSONUtil.toJsonStr(req));
        SettleAccountUpdateFrozenCmd cmd = settleAccountConverter.convertAccountUpdate4FrozenCmd(req);
        return Mono.just(Result.ok(settleAccountCmdService.handlerAccountFrozen(cmd)));
    }

    /**
     * 解冻账户金额
     * 
     * @param req 金额解冻请求
     * @return 解冻结果
     */
    @PostMapping("/v1/unfrozenAmount")
    public Mono<Result<Boolean>> unfrozenAmount(@RequestBody @Validated SettleAccountAmountUnfrozenReq req) {
        log.info("解冻账户金额, req={}", JSONUtil.toJsonStr(req));
        SettleAccountUpdateUnFrozenCmd cmd = settleAccountConverter.convertSettleAccountUpdateUnFrozenCmd(req);
        return Mono.just(Result.ok(settleAccountCmdService.handlerAccountUnFrozen(cmd)));
    }

    // ===================== 账户流水管理接口 =====================

    /**
     * 分页查询账户流水
     * 
     * @param req 流水分页查询请求
     * @return 流水分页列表
     */
    @PostMapping("/v1/pageAccountFlowList")
    public Mono<PageResult<SettleAccountFlowVO>> pageAccountFlow(@RequestBody @Validated SettleAccountFlowPageReq req) {
        log.info("分页查询账户流水, req={}", JSONUtil.toJsonStr(req));
        SettleAccountFlowPageParam param = settleFlowConverter.convertAccountFlowPageParam(req);
        Page<SettleAccountFlow> page = settleFlowQueryService.pageAccountFlowList(param);
        List<SettleAccountFlowVO> voList = settleFlowConverter.convertAccountFlowVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出账户流水
     * 
     * @param req 流水导出请求
     * @return 导出结果
     */
    @PostMapping("/v1/exportAccountFlowList")
    public Mono<Result<String>> exportAccountFlowList(@RequestBody @Validated SettleAccountFlowPageReq req) {
        log.info("导出账户流水, req={}", JSONUtil.toJsonStr(req));
        SettleAccountFlowPageParam param = settleFlowConverter.convertAccountFlowPageParam(req);
        return Mono.just(Result.ok(settleFlowQueryService.exportAccountFlowList(param)));
    }

    // ===================== 账户快照管理接口 =====================

    /**
     * 获取账户余额快照
     * 
     * @param req 快照查询请求
     * @return 账户余额快照
     */
    @PostMapping("/v1/getAccountSnapshot")
    public Mono<Result<AccountSnapshotDTO>> getAccountSnapshot(@RequestBody @Validated SettleAccountSnapshotReq req) {
        log.info("获取账户余额快照, req={}", JSONUtil.toJsonStr(req));
        SettleAccountSnapshotParam param = settleAccountSnapshotConverter.convertAccountSnapshotParam(req);
        return Mono.just(Result.ok(settleAccountSnapshotQueryService.getAccountSnapshot(param)));
    }

    // ===================== 结算订单管理接口 =====================

    /**
     * 分页查询结算订单
     * 
     * @param req 订单分页查询请求
     * @return 订单分页列表
     */
    @PostMapping("/v1/pageSettleOrderList")
    public Mono<PageResult<SettleOrderDTO>> pageSettleOrderList(@RequestBody SettleOrderPageReq req) {
        log.info("分页查询结算订单, req={}", JSONUtil.toJsonStr(req));
        SettleOrderPageParam param = settleConverter.convertSettleOrderPageParam(req);
        Page<SettleOrder> page = settleOrderQueryService.pageSettleOrderList(param);
        List<SettleOrderDTO> dtoList = settleConverter.convertSettleOrderDTOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), dtoList));
    }

    /**
     * 获取结算订单详情
     * 
     * @param req 订单查询请求
     * @return 订单详情
     */
    @PostMapping("/v1/getSettleOrder")
    public Mono<Result<SettleOrder>> getSettleOrder(@RequestBody SettleOrderReq req) {
        log.info("获取结算订单详情, req={}", JSONUtil.toJsonStr(req));
        SettleOrderParam param = settleConverter.convertSettleOrderParam(req);
        return Mono.just(Result.ok(settleOrderQueryService.getSettleOrder(param)));
    }

    /**
     * 补充结算订单
     * 
     * @param req 补充请求
     * @return 补充结果
     */
    @PostMapping("/v1/supplement")
    public Mono<Result<Boolean>> supplement(@RequestBody @Validated SettleSupplementReq req) {
        log.info("补充结算订单, req={}", JSONUtil.toJsonStr(req));
        SettleSupplementCmd cmd = settleConverter.convertSettleSupplementCmd(req);
        return Mono.just(Result.ok(settleOrderCmdService.supplement(cmd)));
    }

    /**
     * 退款结算订单
     * 
     * @param req 退款请求
     * @return 退款结果
     */
    @PostMapping("/v1/refund")
    public Mono<Result<Boolean>> refund(@RequestBody @Validated SettleRefundReq req) {
        log.info("退款结算订单, req={}", JSONUtil.toJsonStr(req));
        SettleRefundCmd cmd = settleConverter.convertSettleRefundCmd(req);
        return Mono.just(Result.ok(settleOrderCmdService.refund(cmd)));
    }

    // ===================== 结算统计管理接口 =====================

    /**
     * 获取结算实时统计指标
     * 
     * @param req 统计查询请求
     * @return 统计指标
     */
    @PostMapping("/v1/getSettleTimelyStatistics4Index")
    public Mono<Result<SettleTimelyStatisticsIndexDTO>> getSettleTimelyStatistics4Index(@RequestBody @Validated SettleTimelyStatisticsIndexReq req) {
        log.info("获取结算实时统计指标, req={}", JSONUtil.toJsonStr(req));
        SettleTimelyStatisticsIndexParam param = settleStatisticsConverter.convertSettleTimelyStatisticsIndexParam(req);
        return Mono.just(Result.ok(settleStatisticsQueryService.getSettleTimelyStatistics4Index(param)));
    }
}
