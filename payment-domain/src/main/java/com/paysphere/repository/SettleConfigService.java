package com.paysphere.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paysphere.db.entity.SettleConfig;

public interface SettleConfigService extends IService<SettleConfig> {

    boolean getSettlementSwitch();

    boolean updateSettlementSwitch(Boolean settlementSwitch);
}


