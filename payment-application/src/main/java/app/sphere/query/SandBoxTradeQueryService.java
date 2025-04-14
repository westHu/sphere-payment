package app.sphere.query;

import app.sphere.query.dto.CashierDTO;
import app.sphere.query.dto.CashierPaymentMethodDTO;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.SandboxTradePaymentOrderPageDTO;
import app.sphere.query.dto.SandboxTradePayoutOrderPageDTO;
import app.sphere.query.param.CashierParam;
import app.sphere.query.param.SandboxTradePaymentOrderPageParam;
import app.sphere.query.param.SandboxTradePayoutOrderPageParam;
import app.sphere.query.param.TradePaymentLinkPageParam;
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
