package app.sphere.query;

import app.sphere.query.dto.AccountSnapshotDTO;
import app.sphere.query.param.SettleAccountSnapshotParam;

public interface SettleAccountSnapshotQueryService {

    AccountSnapshotDTO getAccountSnapshot(SettleAccountSnapshotParam param);

}
