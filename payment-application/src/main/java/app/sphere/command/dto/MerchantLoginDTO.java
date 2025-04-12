package app.sphere.command.dto;

import app.sphere.query.dto.MerchantBaseDTO;
import app.sphere.query.dto.MerchantConfigDTO;
import app.sphere.query.dto.MerchantOperatorDTO;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantOperator;
import lombok.Data;

import java.util.List;

@Data
public class MerchantLoginDTO {

    /**
     * 访问秘钥
     */
    private String accessToken;

    /**
     * 商户基本信息
     */
    private Merchant merchant;

    /**
     * 商户操作员 1对多
     */
    private MerchantOperator merchantOperator;
}
