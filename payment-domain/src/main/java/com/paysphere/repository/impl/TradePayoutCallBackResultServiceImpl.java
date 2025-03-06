package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradePayoutCallBackResult;
import com.paysphere.db.mapper.TradePayoutCallBackResultMapper;
import com.paysphere.repository.TradePayoutCallBackResultService;
import org.springframework.stereotype.Service;

@Service
public class TradePayoutCallBackResultServiceImpl extends ServiceImpl<TradePayoutCallBackResultMapper,
        TradePayoutCallBackResult>
        implements TradePayoutCallBackResultService {
}
