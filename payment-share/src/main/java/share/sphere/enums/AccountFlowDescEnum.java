package share.sphere.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountFlowDescEnum {

    INCOME_TRANSFER("收入:资金转入", AccountDirectionEnum.INCOME),
    EXPEND_TRANSFER("支出:资金转出", AccountDirectionEnum.EXPEND),

    /**
     * 商户
     */
    MERCHANT_INCOME_PAYMENT("商户收入:商户收款", AccountDirectionEnum.INCOME),
    MERCHANT_EXPEND_FEE("商户支出:商户手续费", AccountDirectionEnum.EXPEND),
    MERCHANT_EXPEND_PAYOUT("商户支出:商户出款", AccountDirectionEnum.EXPEND),
    MERCHANT_EXPEND_REFUND("商户支出:商户退款", AccountDirectionEnum.EXPEND),
    MERCHANT_EXPEND_FROZEN("商户支出:商户冻结", AccountDirectionEnum.EXPEND),
    MERCHANT_INCOME_UNFROZEN("商户收入:商户解冻", AccountDirectionEnum.INCOME),
    MERCHANT_INCOME_RECHARGE("商户收入:商户充值", AccountDirectionEnum.INCOME),
    MERCHANT_EXPEND_WITHDRAW("商户支出:商户提现", AccountDirectionEnum.EXPEND),

    /**
     * 平台
     */
    PLATFORM_INCOME_PAYMENT_FEE(" 平台收入:收款-商户手续费", AccountDirectionEnum.INCOME),
    PLATFORM_EXPEND_PAYMENT_CHANNEL(" 平台支出:收款-通道成本", AccountDirectionEnum.EXPEND),

    PLATFORM_INCOME_PAYOUT_FEE(" 平台收入:出款-商户手续费", AccountDirectionEnum.INCOME),
    PLATFORM_EXPEND_PAYOUT_CHANNEL(" 平台支出:出款-通道成本费用", AccountDirectionEnum.EXPEND),
    PLATFORM_EXPEND_REFUND(" 平台支出:商户退款", AccountDirectionEnum.EXPEND),
    PLATFORM_INCOME_WITHDRAW_FEE(" 平台收入:商户提现手续费", AccountDirectionEnum.INCOME),
    PLATFORM_EXPEND_WITHDRAW_CHANNEL(" 平台支出:商户提现通道成本", AccountDirectionEnum.EXPEND),
    PLATFORM_EXPEND_MERCHANT_PROFIT(" 平台支出:商户分润费用", AccountDirectionEnum.EXPEND),

    ;

    private final String name;
    private final AccountDirectionEnum accountDirection;
}
