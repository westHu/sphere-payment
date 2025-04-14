package app.sphere.query.param;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TradeDetailParam {

    private String merchantId;

    private LocalDate tradeDate = LocalDate.now().plusDays(-1);

}
