package com.paysphere.convert;

import com.paysphere.command.cmd.SettleAccountAddCmd;
import com.paysphere.command.cmd.SettleAccountUpdateFrozenCmd;
import com.paysphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import com.paysphere.controller.request.SettleAccountAddReq;
import com.paysphere.controller.request.SettleAccountAmountFrozenReq;
import com.paysphere.controller.request.SettleAccountAmountUnfrozenReq;
import com.paysphere.controller.request.SettleAccountDropReq;
import com.paysphere.controller.request.SettleAccountListReq;
import com.paysphere.controller.request.SettleAccountPageReq;
import com.paysphere.controller.response.SettleAccountVO;
import com.paysphere.db.entity.SettleAccount;
import com.paysphere.query.param.SettleAccountDropParam;
import com.paysphere.query.param.SettleAccountListParam;
import com.paysphere.query.param.SettleAccountPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface SettleAccountConverter {

    SettleAccountAddCmd convertSettleAccountAddCmd(SettleAccountAddReq req);

    SettleAccountPageParam convertAccountPageParam(SettleAccountPageReq req);

    List<SettleAccountVO> convertAccountVOList(List<SettleAccount> records);

    SettleAccountUpdateFrozenCmd convertAccountUpdate4FrozenCmd(SettleAccountAmountFrozenReq req);

    SettleAccountUpdateUnFrozenCmd convertSettleAccountUpdateUnFrozenCmd(SettleAccountAmountUnfrozenReq req);

    SettleAccountDropParam convertSettleAccountDropParam(SettleAccountDropReq req);

    SettleAccountListParam convertSettleAccountListParam(SettleAccountListReq req);
}
