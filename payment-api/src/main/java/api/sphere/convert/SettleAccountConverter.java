package api.sphere.convert;

import api.sphere.controller.request.SettleAccountAddReq;
import api.sphere.controller.request.SettleAccountAmountFrozenReq;
import api.sphere.controller.request.SettleAccountAmountUnfrozenReq;
import api.sphere.controller.request.SettleAccountDropReq;
import api.sphere.controller.request.SettleAccountListReq;
import api.sphere.controller.request.SettleAccountPageReq;
import api.sphere.controller.response.SettleAccountVO;
import app.sphere.command.cmd.SettleAccountAddCmd;
import app.sphere.command.cmd.SettleAccountUpdateFrozenCmd;
import app.sphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import app.sphere.query.param.SettleAccountDropParam;
import app.sphere.query.param.SettleAccountListParam;
import app.sphere.query.param.SettleAccountPageParam;
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
