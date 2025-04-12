package infrastructure.sphere.remote.admin;

import lombok.Data;

@Data
public class TradeExamineParam {
    private String tradeNo;
    private String orderNo;
    private String merchantId;
    private String merchantName;
    private String productDetail;
    private String itemDetailInfo;
    private String paymentMethod;
    private String cashAccount;
    private String currency;
    private String amount;
    private String payerInfo;
    private String receiverInfo;
    private String remark;
    private String applyOperator;
}
