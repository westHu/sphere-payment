package app.sphere.query;

import app.sphere.query.dto.*;
import app.sphere.query.param.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.SandboxTradePaymentLinkOrder;

import java.util.List;

public interface SandBoxTradeQueryService {

    PageDTO<SandboxTradePaymentOrderPageDTO> pageSandBoxPayOrderList(SandboxTradePaymentOrderPageParam param);

    PageDTO<SandboxTradePayoutOrderPageDTO> pageSandboxCashOrderList(SandboxTradePayoutOrderPageParam param);

    CashierDTO getSandboxCashier(CashierParam param);

    Page<SandboxTradePaymentLinkOrder> pageSandboxPaymentLinkList(TradePaymentLinkPageParam param);

    String getSandboxMerchantStep(String merchantId);

    List<CashierPaymentMethodDTO> getSandboxPaymentMethodList4Cashier();

}
