package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.command.SettleFileJobCmdService;
import com.paysphere.command.cmd.SettleFileJobCommand;
import com.paysphere.command.dto.SettleFileDTO;
import com.paysphere.db.entity.BaseEntity;
import com.paysphere.db.entity.SettleOrder;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.repository.SettleOrderService;
import com.paysphere.utils.StorageUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.paysphere.TradeConstant.DF_0;


@Slf4j
@Service
public class SettleFileJobCmdServiceImpl implements SettleFileJobCmdService {


    private static final int MIN_SIZE = 5;
    private static final int MAX_SIZE = 5000;

    @Resource
    SettleOrderService settleOrderService;



    @Override
    public void handlerSettleFile(SettleFileJobCommand command) {
        log.info("handlerSettleFile command={}", JSONUtil.toJsonStr(command));

        String tradeDate = command.getTradeDate();
        String fileNameDate = tradeDate.replace("-", "");

        //每日结算记录文件
        handlerSettleFile(command, fileNameDate);
    }


    /**
     * 每日结算订单总和 : 校验订单的状态机\订单丢失等情况
     */
    private void handlerSettleFile(SettleFileJobCommand command, String fileNameDate) {
        List<SettleFileDTO> fileDTOList = new ArrayList<>();
        String tradeDate = command.getTradeDate();
        String startTime = tradeDate + " 00:00:00";
        String endTime = tradeDate + " 23:59:59";

        long minId = 0;
        int pageSize = MIN_SIZE;
        do {
            QueryWrapper<SettleOrder> settleQuery = new QueryWrapper<>();
            settleQuery.select("id, " +
                    "trade_no as tradeNo, " +
                    "trade_type as tradeType, " +
                    "trade_time as tradeTime, " +
                    "channel_code as channelCode, " +
                    "channel_name as channelName, " +
                    "currency, " +
                    "amount, " +
                    "merchant_fee as merchantFee, " +
                    "merchant_profit as merchantProfit, " +
                    "account_amount as accountAmount, " +
                    "merchant_profit as merchantProfit, " +
                    "channel_cost as channelCost, " +
                    "platform_profit as platformProfit");
            settleQuery.lambda().between(SettleOrder::getTradeTime, startTime, endTime)
                    .eq(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_SUCCESS.getCode());
            if (minId > 0) {
                settleQuery.lambda().gt(SettleOrder::getId, minId).orderByAsc(SettleOrder::getId);
                pageSize = MAX_SIZE;
            }
            settleQuery.lambda().last("LIMIT " + pageSize);
            List<SettleOrder> settleOrderList = settleOrderService.list(settleQuery);
            if (CollectionUtils.isEmpty(settleOrderList)) {
                break;
            }
            log.info("handlerSettleFile payOrderList size={}", settleOrderList.size());

            //这里取最小ID 逻辑更新
            if (pageSize == MIN_SIZE) {
                minId = settleOrderList.stream().map(BaseEntity::getId).min(Long::compareTo).get();
                minId = minId - 1;
                continue;
            } else {
                minId = settleOrderList.get(settleOrderList.size() - 1).getId();
            }
            log.info("handlerSettleFile minId={}", minId);

            List<SettleFileDTO> collect = settleOrderList.stream()
                    .map(e -> {
                        SettleFileDTO fileDTO = new SettleFileDTO();
                        fileDTO.setTradeNo(e.getTradeNo());
                        fileDTO.setTradeType(e.getTradeType() + "");
                        fileDTO.setTradeTime(e.getTradeTime().format(DF_0));
                        fileDTO.setChannelCode(e.getChannelCode());
                        fileDTO.setChannelName(e.getChannelName());
                        fileDTO.setCurrency(e.getCurrency());
                        fileDTO.setAmount(parseAmountStr(e.getAmount()));
                        fileDTO.setMerchantFee(parseAmountStr(e.getMerchantFee()));
                        fileDTO.setAccountAmount(parseAmountStr(e.getAccountAmount()));
                        fileDTO.setMerchantProfit(parseAmountStr(e.getMerchantProfit()));
                        fileDTO.setChannelCost(parseAmountStr(e.getChannelCost()));
                        fileDTO.setPlatformProfit(parseAmountStr(e.getPlatformProfit()));
                        return fileDTO;
                    }).toList();
            fileDTOList.addAll(collect);

            //如果小于size， 说明也没有下一页，则跳出
            if (settleOrderList.size() < MAX_SIZE) {
                break;
            }
        } while (true);


        log.info("handlerSettleFile dtoList={}", fileDTOList.size());
        log.info("handlerTradePayFile FirstOne={}", JSONUtil.toJsonStr(fileDTOList.get(0)));

        //文件、csv、存储
        String fileName = StorageUtil.settlementCsvFile(fileNameDate);
        log.info("handlerSettleFile fileName={}", fileName);

        //如果存在, 则先删除
//        boolean deleteIfExist = storageHandler.deleteIfExist(fileName);
//        log.info("handlerSettleFile deleteIfExist={}", deleteIfExist);

        // 上传谷歌
//        String uploadPath = storageHandler.uploadObject(fileDTOList, SYSTEM, fileName, "SettleFile");
//        log.info("handlerSettleFile uploadPath={}", uploadPath);
    }

    /**
     * 金额转字符串
     */
    private String parseAmountStr(BigDecimal amount) {
        if (Objects.isNull(amount)) {
            return StringUtils.EMPTY;
        }
        return amount.setScale(0, RoundingMode.UP).toString();
    }

}
