package com.paysphere.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.SettleAccount;
import com.paysphere.query.dto.SettleAccountDTO;
import com.paysphere.query.dto.SettleAccountDropDTO;
import com.paysphere.query.param.SettleAccountDropParam;
import com.paysphere.query.param.SettleAccountListParam;
import com.paysphere.query.param.SettleAccountPageParam;
import com.paysphere.query.param.SettleAccountParam;

import java.util.List;

public interface SettleAccountQueryService {

    List<SettleAccountDropDTO> dropSettleAccountList(SettleAccountDropParam param);

    Page<SettleAccount> pageSettleAccountList(SettleAccountPageParam command);

    List<SettleAccountDTO> getSettleAccountList(SettleAccountListParam param);

    SettleAccount getSettleAccount(SettleAccountParam param);

}
