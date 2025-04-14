package app.sphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import app.sphere.assembler.ApplicationConverter;
import app.sphere.command.SettleAccountJobCmdService;
import app.sphere.command.cmd.SettleAccountFlowOrderRevisionJobCommand;
import app.sphere.command.cmd.SettleAccountFlowRevisionJobCommand;
import app.sphere.command.cmd.SettleAccountSnapshotJobCommand;
import infrastructure.sphere.db.entity.SettleAccount;
import infrastructure.sphere.db.entity.SettleAccountFlow;
import infrastructure.sphere.db.entity.SettleAccountSnapshot;
import domain.sphere.repository.SettleAccountFlowRepository;
import domain.sphere.repository.SettleAccountRepository;
import domain.sphere.repository.SettleAccountSnapshotRepository;
import domain.sphere.repository.SettleOrderRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static share.sphere.TradeConstant.LIMIT_1;

/**
 * 账户资金快照
 */
@Slf4j
@Service
public class SettleAccountJobCmdServiceImpl implements SettleAccountJobCmdService {

    @Resource
    SettleAccountRepository settleAccountRepository;
    @Resource
    SettleAccountSnapshotRepository settleAccountSnapshotRepository;
    @Resource
    SettleAccountFlowRepository settleAccountFlowRepository;
    @Resource
    ApplicationConverter applicationConverter;

    @Override
    public void accountDailySnapshot(SettleAccountSnapshotJobCommand command) {
        log.info("accountDailySnapshot command={}", JSONUtil.toJsonStr(command));
        LocalDate yesterday = LocalDate.now().plusDays(-1);
        log.info("accountDailySnapshot account date={}", yesterday);

        //删除该日的快照数据（其实也应该没有）
        QueryWrapper<SettleAccountSnapshot> deleteQuery = new QueryWrapper<>();
        deleteQuery.lambda().eq(SettleAccountSnapshot::getAccountDate, yesterday);
        settleAccountSnapshotRepository.remove(deleteQuery);

        //生成余额快照
        List<SettleAccount> accountList = settleAccountRepository.list();
        if (CollectionUtils.isEmpty(accountList)) {
            log.warn("AccountJobCmdService query account list is empty");
            return;
        }

        //先查询并生成再统一存储
        List<SettleAccountSnapshot> snapshotList = accountList.stream().map(e -> {
            SettleAccountSnapshot snapshot = applicationConverter.convertAccountDailySnapshot(e);
            snapshot.setAccountDate(yesterday);
            snapshot.setCreateTime(LocalDateTime.now());
            snapshot.setUpdateTime(null);
            return snapshot;
        }).toList();

        if (CollectionUtils.isEmpty(snapshotList)) {
            log.warn("AccountJobCmdService build snapshot list is empty");
            return;
        }
        boolean saveBatch = settleAccountSnapshotRepository.saveBatch(snapshotList);
        log.info("accountDailySnapshot saveBatch result={}", saveBatch);
        if (!saveBatch) {
            log.warn("accountDailySnapshot saveBatch error");
        }
    }

    @Override
    public void accountFlowRevision(SettleAccountFlowRevisionJobCommand command) {
        log.info("accountFlowRevision time={}, command={}", LocalDateTime.now(), JSONUtil.toJsonStr(command));
        String merchantId = command.getMerchantId();
        String accountNo = command.getAccountNo();

        //查询余额、耗时少
        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.lambda()
                .eq(StringUtils.isNotBlank(merchantId), SettleAccount::getMerchantId, merchantId)
                .eq(StringUtils.isNotBlank(accountNo), SettleAccount::getAccountNo, accountNo);
        List<SettleAccount> accountList = settleAccountRepository.list(accountQuery);
        log.info("accountFlowRevision accountList size={}", accountList.size());
        if (CollectionUtils.isEmpty(accountList)) {
            return;
        }

        //查询流水的最大ID、耗时少
        QueryWrapper<SettleAccountFlow> flowQuery = new QueryWrapper<>();
        flowQuery.lambda()
                .eq(StringUtils.isNotBlank(merchantId), SettleAccountFlow::getMerchantId, merchantId)
                .eq(StringUtils.isNotBlank(accountNo), SettleAccountFlow::getAccountNo, accountNo)
                .orderByDesc(SettleAccountFlow::getId) //1
                .last(LIMIT_1);
        SettleAccountFlow accountFlow = settleAccountFlowRepository.getOne(flowQuery);
        Long flowId = 1L;
        if (Objects.nonNull(accountFlow)) {
            flowId = accountFlow.getId();
        }
        log.info("accountFlowRevision accountFlow flowId={}", flowId);

        //查询分组流水、 耗时大
        QueryWrapper<SettleAccountFlow> flowGroupQuery = new QueryWrapper<>();
        flowGroupQuery.select("account_no as accountNo, IFNULL(sum(amount), 0) as amount");
        flowGroupQuery.lambda()
                .eq(StringUtils.isNotBlank(accountNo), SettleAccountFlow::getAccountNo, accountNo)
                .le(SettleAccountFlow::getId, flowId)
                .groupBy(SettleAccountFlow::getAccountNo);
        List<SettleAccountFlow> groupAccountFlowList = settleAccountFlowRepository.list(flowGroupQuery);
        log.info("accountFlowRevision groupAccountFlowList={}", JSONUtil.toJsonStr(groupAccountFlowList));
        if (CollectionUtils.isEmpty(groupAccountFlowList)) {
            return;
        }

        for (SettleAccount account : accountList) {
            handlerFlowRevision(groupAccountFlowList, account, command);
        }
    }

    @Override
    public void accountFlowOrderRevision(SettleAccountFlowOrderRevisionJobCommand command) {
        log.info("accountFlowRevision time={}, command={}", LocalDateTime.now(), JSONUtil.toJsonStr(command));
    }




    //===============================================================================================================

    /**
     * 余额&流水对账
     */
    private void handlerFlowRevision(List<SettleAccountFlow> groupAccountFlowList, SettleAccount account, SettleAccountFlowRevisionJobCommand command) {
        String accountNo = account.getAccountNo();
        log.info("handlerFlowRevision accountNo={}", accountNo);
        BigDecimal balanceAmount = account.getAvailableBalance();
        if (balanceAmount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        //筛选出对应账户的流水汇总金额
        BigDecimal flowSumAmount = groupAccountFlowList.stream().filter(e -> e.getAccountNo().equals(accountNo))
                .map(SettleAccountFlow::getAmount)
                .findAny()
                .orElse(BigDecimal.ZERO);
        log.info("accountFlowRevision balanceAmount={} flowSumAmount={}", balanceAmount, flowSumAmount);

        if (balanceAmount.compareTo(flowSumAmount) != 0) {
            String msg = "余额不平！账户号:" + accountNo + ", 可用余额:" + balanceAmount + ", 流水汇总金额: " + flowSumAmount;
            log.error(msg);
        }
    }

}
