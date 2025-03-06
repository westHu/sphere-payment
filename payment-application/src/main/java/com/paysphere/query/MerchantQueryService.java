package com.paysphere.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.Merchant;
import com.paysphere.query.dto.MerchantDropDTO;
import com.paysphere.query.dto.MerchantTradeDTO;
import com.paysphere.query.param.MerchantDropListParam;
import com.paysphere.query.param.MerchantIdParam;
import com.paysphere.query.param.MerchantPageParam;
import com.paysphere.query.param.OptionalMerchantDetailParam;

import java.util.List;

public interface MerchantQueryService {

    List<MerchantDropDTO> dropMerchantList(MerchantDropListParam param);

    Page<Merchant> pageBaseMerchantList(MerchantPageParam param);

    Merchant getBaseMerchant(MerchantIdParam param);

    Merchant getMerchant(String merchantId);

    MerchantTradeDTO getMerchantTradeDTO(OptionalMerchantDetailParam param);
}
