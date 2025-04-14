package app.sphere.query;

import app.sphere.query.dto.MerchantDropDTO;
import app.sphere.query.dto.MerchantTradeDTO;
import app.sphere.query.param.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.Merchant;

import java.util.List;

public interface MerchantQueryService {

    List<MerchantDropDTO> dropMerchantList(MerchantDropListParam param);

    Page<Merchant> pageBaseMerchantList(MerchantPageParam param);

    Merchant getMerchant(MerchantIdParam param);

    MerchantTradeDTO getMerchantTradeDTO(MerchantTradeParam param);
}
