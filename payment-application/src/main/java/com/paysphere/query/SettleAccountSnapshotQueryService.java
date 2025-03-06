package com.paysphere.query;

import com.paysphere.query.dto.AccountSnapshotDTO;
import com.paysphere.query.param.SettleAccountSnapshotParam;

public interface SettleAccountSnapshotQueryService {

    AccountSnapshotDTO getAccountSnapshot(SettleAccountSnapshotParam param);

}
