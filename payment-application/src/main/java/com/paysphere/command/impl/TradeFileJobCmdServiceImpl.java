package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.command.TradeFileJobCmdService;
import com.paysphere.command.cmd.TradeFileJobCommand;
import com.paysphere.command.dto.TradeFileDTO;
import com.paysphere.db.entity.BaseEntity;
import com.paysphere.db.entity.TradePaymentOrder;
import com.paysphere.db.entity.TradePayoutOrder;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.repository.TradePaymentOrderService;
import com.paysphere.repository.TradePayoutOrderService;
import com.paysphere.utils.StorageUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TradeFileJobCmdServiceImpl implements TradeFileJobCmdService {

    private static final int MIN_SIZE = 5;
    private static final int MAX_SIZE = 5000;

    @Resource
    TradePaymentOrderService tradePaymentOrderService;
    @Resource
    TradePayoutOrderService tradePayoutOrderService;



    @Override
    public void handlerTradeFile(TradeFileJobCommand command) {
        log.info("trade csv file command={}", JSONUtil.toJsonStr(command));

        String tradeDate = command.getTradeDate();
        String fileNameDate = tradeDate.replace("-", "");

        // 查询 -> 分页
        handlerTradeFile(command, fileNameDate);
    }


    /**
     * 处理收款订单文件
     */
    @SneakyThrows
    private void handlerTradeFile(TradeFileJobCommand command, String fileNameDate) {
        List<TradeFileDTO> fileDTOList = new ArrayList<>();
        String tradeDate = command.getTradeDate();
        String startTime = tradeDate + " 00:00:00";
        String endTime = tradeDate + " 23:59:59";
        log.info("handlerTradeFile startTime={}, endTime={}", startTime, endTime);

        //收款分页查询
        handlerPayFile(fileDTOList, startTime, endTime);
        //代付分页查询
        handlerCashFile(fileDTOList, startTime, endTime);
        log.info("payspherepay handlerTradeFile dtoList={}", fileDTOList.size());

        //处理空记录
        handlerListIsEmpty(fileDTOList);
        log.info("payspherepay handlerTradeFile FirstOne={}", JSONUtil.toJsonStr(fileDTOList.get(0)));

        String fileName = StorageUtil.settlementCsvFile(fileNameDate);
        log.info("payspherepay handlerTradeFile fileName={}", fileName);

        //如果存在, 则先删除
//        boolean deleteIfExist = storageHandler.deleteIfExist(fileName);
//        log.info("payspherepay handlerTradeFile deleteIfExist={}", deleteIfExist);

        // 上传谷歌
//        String uploadPath = storageHandler.uploadObject(fileDTOList, TradeConstant.SYSTEM, fileName, "TradeFile");
//        log.info("payspherepay handlerTradeFile uploadPath={}", uploadPath);
    }


    /**
     * 处理空记录
     */
    private void handlerListIsEmpty(List<TradeFileDTO> fileDTOList) {
        if (CollectionUtils.isEmpty(fileDTOList)) {
            TradeFileDTO fileDTO = new TradeFileDTO();
            fileDTO.setTradeNo(TradeConstant.FILE_DEFAULT_FILED);
            fileDTO.setOuterNo(TradeConstant.FILE_DEFAULT_FILED);
            fileDTO.setMerchantId(TradeConstant.FILE_DEFAULT_FILED);
            fileDTO.setMerchantName(TradeConstant.FILE_DEFAULT_FILED);
            fileDTO.setTradeType(TradeConstant.FILE_DEFAULT_FILED);
            fileDTO.setPaymentMethod(TradeConstant.FILE_DEFAULT_FILED);
            fileDTO.setChannelCode(TradeConstant.FILE_DEFAULT_FILED);
            fileDTO.setChannelName(TradeConstant.FILE_DEFAULT_FILED);

            fileDTO.setTradeTime(LocalDateTime.now().format(TradeConstant.DF_0));
            fileDTO.setPaymentFinishTime(fileDTO.getTradeTime());

            fileDTO.setCurrency(TradeConstant.FILE_DEFAULT_FILED);
            fileDTO.setAmount("0"); //注意这里的金额
            fileDTO.setMerchantFee("0");
            fileDTO.setAccountAmount("0");
            fileDTO.setMerchantProfit("0");
            fileDTO.setChannelCost("0");
            fileDTO.setPlatformProfit("0");

            fileDTO.setArea(10);
            fileDTOList.add(fileDTO);
        }
    }

    /**
     * 处理收款文件
     */
    private void handlerPayFile(List<TradeFileDTO> fileDTOList, String startTime, String endTime) {
        log.info("handlerTradeFile handlerPayFile begin={}", LocalDateTime.now());

        long minId = 0;
        int pageSize = MIN_SIZE;
        do {
            QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
            payOrderQuery.select("id, " +
                    "trade_no as tradeNo, " +
                    "outer_no as outerNo, " +
                    "trade_time as tradeTime, " +
                    "payment_finish_time as paymentFinishTime, " +
                    "payment_method as paymentMethod, " +
                    "channel_code as channelCode, " +
                    "merchant_id as merchantId, " +
                    "merchant_name as merchantName, " +

                    "currency, " +
                    "amount, " +
                    "merchant_fee as merchantFee, " +
                    "account_amount as accountAmount, " +
                    "merchant_profit as merchantProfit, " +
                    "channel_cost as channelCost, " +
                    "platform_profit as platformProfit, " +
                    "area");
            payOrderQuery.lambda().between(TradePaymentOrder::getTradeTime, startTime, endTime)
                    .eq(TradePaymentOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_SUCCESS.getCode());

            if (minId > 0) {
                payOrderQuery.lambda().gt(TradePaymentOrder::getId, minId).orderByAsc(TradePaymentOrder::getId);
                pageSize = MAX_SIZE;
            }
            payOrderQuery.lambda().last("LIMIT " + pageSize);
            List<TradePaymentOrder> payOrderList = tradePaymentOrderService.list(payOrderQuery);
            if (CollectionUtils.isEmpty(payOrderList)) {
                break;
            }
            log.info("payspherepay handlerPayFile payOrderList size={}", payOrderList.size());

            //这里取最小ID 逻辑更新
            if (pageSize == MIN_SIZE) {
                minId = payOrderList.stream().map(BaseEntity::getId).min(Long::compareTo).get();
                minId = minId - 1;
                continue;
            } else {
                minId = payOrderList.get(payOrderList.size() - 1).getId();
            }
            log.info("payspherepay handlerPayFile minId={}", minId);

            List<TradeFileDTO> collect = payOrderList.stream().map(e -> {


                TradeFileDTO fileDTO = new TradeFileDTO();
                fileDTO.setTradeNo(e.getTradeNo());
                fileDTO.setOuterNo(e.getOrderNo());
                fileDTO.setTradeType(TradeTypeEnum.PAYMENT.getCode() + "");
                fileDTO.setMerchantId(e.getMerchantId());
                fileDTO.setMerchantName(e.getMerchantName());
                fileDTO.setPaymentMethod(e.getPaymentMethod());
                fileDTO.setChannelCode(e.getChannelCode());
                fileDTO.setChannelName(e.getChannelName());

//                fileDTO.setTradeTime(e.getTradeTime().format(TradeConstant.DF_0));
//                fileDTO.setPaymentFinishTime(e.getPaymentFinishTime().format(TradeConstant.DF_0));

                fileDTO.setCurrency(e.getCurrency());
                fileDTO.setAmount(parseAmountStr(e.getAmount()));
                fileDTO.setMerchantFee(parseAmountStr(e.getMerchantFee()));
                fileDTO.setAccountAmount(parseAmountStr(e.getAccountAmount()));
                fileDTO.setMerchantProfit(parseAmountStr(e.getMerchantProfit()));
                fileDTO.setChannelCost(parseAmountStr(e.getChannelCost()));
                fileDTO.setPlatformProfit(parseAmountStr(e.getPlatformProfit()));

                fileDTO.setArea(e.getArea());
                return fileDTO;
            }).toList();
            fileDTOList.addAll(collect);

            if (payOrderList.size() < pageSize) {
                break;
            }
        } while (true);


        log.info("handlerTradeFile handlerPayFile end={}", LocalDateTime.now());
    }



    /**
     * 处理代付文件
     */
    private void handlerCashFile(List<TradeFileDTO> fileDTOList, String startTime, String endTime) {
        log.info("handlerTradeFile handlerCashFile begin={}", LocalDateTime.now());

        long minId = 0;
        int pageSize = MIN_SIZE;
        do {
            QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
            cashOrderQuery.select("id, " +
                    "trade_no as tradeNo, " +
                    "outer_no as outerNo, " +
                    "trade_time as tradeTime, " +
                    "payment_finish_time as paymentFinishTime, " +
                    "payment_method as paymentMethod, " +
                    "channel_code as channelCode, " +
                    "merchant_id as merchantId, " +
                    "merchant_name as merchantName, " +

                    "currency, " +
                    "amount, " +
                    "actual_amount as actualAmount, " +
                    "merchant_fee as merchantFee, " +
                    "account_amount as accountAmount, " +
                    "merchant_profit as merchantProfit, " +
                    "channel_cost as channelCost, " +
                    "platform_profit as platformProfit, " +
                    "area");
            cashOrderQuery.lambda().between(TradePayoutOrder::getTradeTime, startTime, endTime)
                    .eq(TradePayoutOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_SUCCESS.getCode());

            if (minId > 0) {
                cashOrderQuery.lambda().gt(TradePayoutOrder::getId, minId).orderByAsc(TradePayoutOrder::getId);
                pageSize = MAX_SIZE;
            }
            cashOrderQuery.lambda().last("LIMIT " + pageSize);
            List<TradePayoutOrder> cashOrderList = tradePayoutOrderService.list(cashOrderQuery);
            if (CollectionUtils.isEmpty(cashOrderList)) {
                break;
            }
            log.info("payspherepay handlerCashFile cashOrderList size={}", cashOrderList.size());

            //这里取最小ID 逻辑更新
            if (pageSize == MIN_SIZE) {
                minId = cashOrderList.stream().map(BaseEntity::getId).min(Long::compareTo).get();
                minId = minId - 1;
                continue;
            } else {
                minId = cashOrderList.get(cashOrderList.size() - 1).getId();
            }
            log.info("payspherepay handlerCashFile minId={}", minId);

            List<TradeFileDTO> collect = cashOrderList.stream().map(e -> {

                TradeFileDTO fileDTO = new TradeFileDTO();
                fileDTO.setTradeNo(e.getTradeNo());
                fileDTO.setOuterNo(e.getOuterNo());
                fileDTO.setTradeType(TradeTypeEnum.PAYOUT.getCode() + "");
                fileDTO.setMerchantId(e.getMerchantId());
                fileDTO.setMerchantName(e.getMerchantName());
                fileDTO.setPaymentMethod(e.getPaymentMethod());
                fileDTO.setChannelCode(e.getChannelCode());
                fileDTO.setChannelName(e.getChannelName());

//                fileDTO.setTradeTime(e.getTradeTime().format(TradeConstant.DF_0));
//                fileDTO.setPaymentFinishTime(e.getPaymentFinishTime().format(TradeConstant.DF_0));

                fileDTO.setCurrency(e.getCurrency());
                fileDTO.setAmount(parseAmountStr(e.getActualAmount())); //注意这里的金额
                fileDTO.setMerchantFee(parseAmountStr(e.getMerchantFee()));
                fileDTO.setAccountAmount(parseAmountStr(e.getAccountAmount()));
                fileDTO.setMerchantProfit(parseAmountStr(e.getMerchantProfit()));
                fileDTO.setChannelCost(parseAmountStr(e.getChannelCost()));
                fileDTO.setPlatformProfit(parseAmountStr(e.getPlatformProfit()));

                fileDTO.setArea(e.getArea());
                return fileDTO;
            }).toList();
            fileDTOList.addAll(collect);

            //如果小于size， 说明也没有下一页，则跳出
            if (cashOrderList.size() < pageSize) {
                break;
            }
        } while (true);

        log.info("handlerTradeFile handlerCashFile end={}", LocalDateTime.now());
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
