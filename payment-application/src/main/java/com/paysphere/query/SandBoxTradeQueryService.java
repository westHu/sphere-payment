package com.paysphere.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.SandboxTradePaymentLinkOrder;
import com.paysphere.query.dto.CashierDTO;
import com.paysphere.query.dto.CashierPaymentMethodDTO;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.dto.SandboxTradeCashOrderPageDTO;
import com.paysphere.query.dto.SandboxTradePayOrderPageDTO;
import com.paysphere.query.param.CashierParam;
import com.paysphere.query.param.SandboxTradeCashOrderPageParam;
import com.paysphere.query.param.SandboxTradePayOrderPageParam;
import com.paysphere.query.param.TradePaymentLinkPageParam;

import java.util.List;

public interface SandBoxTradeQueryService {

    PageDTO<SandboxTradePayOrderPageDTO> pageSandBoxPayOrderList(SandboxTradePayOrderPageParam param);

    PageDTO<SandboxTradeCashOrderPageDTO> pageSandboxCashOrderList(SandboxTradeCashOrderPageParam param);

    CashierDTO getSandboxCashier(CashierParam param);

    Page<SandboxTradePaymentLinkOrder> pageSandboxPaymentLinkList(TradePaymentLinkPageParam param);

    String getSandboxMerchantStep(String merchantId);

    List<CashierPaymentMethodDTO> getSandboxPaymentMethodList4Cashier();

}
