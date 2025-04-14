package api.sphere.convert;

import api.sphere.controller.request.*;
import api.sphere.controller.response.SettleAccountVO;
import app.sphere.command.cmd.*;
import app.sphere.query.param.*;
import infrastructure.sphere.db.entity.SettleAccount;
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
