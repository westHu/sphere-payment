package app.sphere.query;

import app.sphere.query.dto.MerchantTradeDTO;
import app.sphere.query.param.MerchantDropListParam;
import app.sphere.query.param.MerchantIdParam;
import app.sphere.query.param.MerchantPageParam;
import app.sphere.query.param.MerchantTradeParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.Merchant;
import app.sphere.query.dto.MerchantDropDTO;

import java.util.List;

public interface MerchantQueryService {

    List<MerchantDropDTO> dropMerchantList(MerchantDropListParam param);

    Page<Merchant> pageBaseMerchantList(MerchantPageParam param);

    Merchant getMerchant(MerchantIdParam param);

    MerchantTradeDTO getMerchantTradeDTO(MerchantTradeParam param);
}
