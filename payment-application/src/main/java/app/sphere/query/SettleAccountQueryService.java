package app.sphere.query;

import app.sphere.query.dto.SettleAccountDTO;
import app.sphere.query.dto.SettleAccountDropDTO;
import app.sphere.query.param.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.SettleAccount;

import java.util.List;

public interface SettleAccountQueryService {

    List<SettleAccountDropDTO> dropSettleAccountList(SettleAccountDropParam param);

    Page<SettleAccount> pageSettleAccountList(SettleAccountPageParam command);

    List<SettleAccountDTO> getSettleAccountList(SettleAccountListParam param);

    SettleAccount getSettleAccount(SettleAccountParam param);

}
