package app.sphere.query;

import app.sphere.query.dto.CashierDTO;
import app.sphere.query.dto.CashierPaymentMethodDTO;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.SandboxTradeCashOrderPageDTO;
import app.sphere.query.dto.SandboxTradePayOrderPageDTO;
import app.sphere.query.param.CashierParam;
import app.sphere.query.param.SandboxTradeCashOrderPageParam;
import app.sphere.query.param.SandboxTradePayOrderPageParam;
import app.sphere.query.param.TradePaymentLinkPageParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.SandboxTradePaymentLinkOrder;

import java.util.List;

public interface SandBoxTradeQueryService {

    PageDTO<SandboxTradePayOrderPageDTO> pageSandBoxPayOrderList(SandboxTradePayOrderPageParam param);

    PageDTO<SandboxTradeCashOrderPageDTO> pageSandboxCashOrderList(SandboxTradeCashOrderPageParam param);

    CashierDTO getSandboxCashier(CashierParam param);

    Page<SandboxTradePaymentLinkOrder> pageSandboxPaymentLinkList(TradePaymentLinkPageParam param);

    String getSandboxMerchantStep(String merchantId);

    List<CashierPaymentMethodDTO> getSandboxPaymentMethodList4Cashier();

}
