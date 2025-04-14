package app.sphere.command.cmd;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantAddCommand extends OperatorCommand {

    /**
     * 用户名
     * 操作员的登录用户名，全局唯一
     */
    private String username;

    /**
     * 密码
     * 操作员的登录密码，加密存储
     */
    private String password;

    /**
     * 商户名称
     * 商户的正式名称
     */
    private String merchantName;

    /**
     * 品牌名称
     * 商户的品牌名称，用于展示
     */
    private String brandName;

    /**
     * 商户性质
     * 1: 个人
     * 2: 企业
     * 3: 机构
     */
    private Integer merchantType;

    /**
     * API对接模式
     * 1: API模式
     * 2: 收银台模式
     * 3: API+收银台模式
     */
    private Integer apiMode;

    /**
     * 支持的币种
     * 商户支持的交易币种列表，如：CNY, USD, EUR等
     */
    private List<String> currencyList;

    /**
     * 商户标签
     */
    private List<String> tags;
}
