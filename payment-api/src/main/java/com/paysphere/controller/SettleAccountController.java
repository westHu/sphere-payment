package com.paysphere.controller;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.command.SettleAccountCmdService;
import com.paysphere.command.cmd.SettleAccountAddCmd;
import com.paysphere.command.cmd.SettleAccountUpdateFrozenCmd;
import com.paysphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import com.paysphere.controller.request.SettleAccountAddReq;
import com.paysphere.controller.request.SettleAccountAmountFrozenReq;
import com.paysphere.controller.request.SettleAccountAmountUnfrozenReq;
import com.paysphere.controller.request.SettleAccountDropReq;
import com.paysphere.controller.request.SettleAccountListReq;
import com.paysphere.controller.request.SettleAccountPageReq;
import com.paysphere.controller.response.SettleAccountVO;
import com.paysphere.convert.SettleAccountConverter;
import com.paysphere.db.entity.SettleAccount;
import com.paysphere.query.SettleAccountQueryService;
import com.paysphere.query.dto.SettleAccountDTO;
import com.paysphere.query.dto.SettleAccountDropDTO;
import com.paysphere.query.param.SettleAccountDropParam;
import com.paysphere.query.param.SettleAccountListParam;
import com.paysphere.query.param.SettleAccountPageParam;
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
public class SettleAccountController {

    @Resource
    SettleAccountConverter settleAccountConverter;
    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettleAccountQueryService settleAccountQueryService;


    /**
     * 查询账户（下拉框）
     */
    @PostMapping("/v1/dropSettleAccountList")
    public Mono<Result<List<SettleAccountDropDTO>>> dropSettleAccountList(@RequestBody SettleAccountDropReq req) {
        log.info("dropSettleAccountList req={}", JSONUtil.toJsonStr(req));

        SettleAccountDropParam param = settleAccountConverter.convertSettleAccountDropParam(req);
        List<SettleAccountDropDTO> dropDTOList = settleAccountQueryService.dropSettleAccountList(param);
        return Mono.just(Result.ok(dropDTOList));
    }

    /**
     * 查询账户列表
     */
    @PostMapping("/v1/pageSettleAccountList")
    public Mono<PageResult<SettleAccountVO>> pageSettleAccountList(@RequestBody @Validated SettleAccountPageReq req) {
        log.info("pageSettleAccountList req={}", JSONUtil.toJsonStr(req));
        SettleAccountPageParam param = settleAccountConverter.convertAccountPageParam(req);

        Page<SettleAccount> page = settleAccountQueryService.pageSettleAccountList(param);
        List<SettleAccountVO> voList = settleAccountConverter.convertAccountVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
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

    /**
     * 新增账户
     */
    @PostMapping("/v1/addSettleAccount")
    public Mono<Result<Boolean>> addSettleAccount(@RequestBody @Validated SettleAccountAddReq req) {
        log.info("addSettleAccount req={}", JSONUtil.toJsonStr(req));
        SettleAccountAddCmd cmd = settleAccountConverter.convertSettleAccountAddCmd(req);

        boolean added = settleAccountCmdService.addSettleAccount(cmd);
        return Mono.just(Result.ok(added));
    }

    /**
     * 冻结金额
     */
    @PostMapping("/v1/frozenAmount")
    public Mono<Result<Boolean>> frozenAmount(@RequestBody @Validated SettleAccountAmountFrozenReq req) {
        log.info("frozenAmount req={}", JSONUtil.toJsonStr(req));
        SettleAccountUpdateFrozenCmd cmd = settleAccountConverter.convertAccountUpdate4FrozenCmd(req);

        boolean frozen = settleAccountCmdService.handlerAccountFrozen(cmd);
        return Mono.just(Result.ok(frozen));
    }

    /**
     * 解冻金额
     */
    @PostMapping("/v1/unfrozenAmount")
    public Mono<Result<Boolean>> unfrozenAmount(@RequestBody @Validated SettleAccountAmountUnfrozenReq req) {
        log.info("unfrozenAmount req={}", JSONUtil.toJsonStr(req));
        SettleAccountUpdateUnFrozenCmd cmd = settleAccountConverter.convertSettleAccountUpdateUnFrozenCmd(req);

        boolean unFrozen = settleAccountCmdService.handlerAccountUnFrozen(cmd);
        return Mono.just(Result.ok(unFrozen));
    }

}
