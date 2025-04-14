package infrastructure.sphere.config.cashier;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "trade.cashier")
public class TradeCashierConfiguration {

    /**
     * 生产收银台前缀链接
     */
    private String path;

    /**
     * 沙箱收银台前缀链接
     */
    private String sandboxPath;

}

