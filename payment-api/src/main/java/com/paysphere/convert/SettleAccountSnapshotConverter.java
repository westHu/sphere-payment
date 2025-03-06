package com.paysphere.convert;


import com.paysphere.controller.request.SettleAccountSnapshotReq;
import com.paysphere.controller.request.SettleAccountSnapshotStatementGroupReq;
import com.paysphere.query.param.SettleAccountSnapshotParam;
import com.paysphere.query.param.SettleAccountSnapshotStatementGroupParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface SettleAccountSnapshotConverter {

    SettleAccountSnapshotParam convertAccountSnapshotParam(SettleAccountSnapshotReq req);

    SettleAccountSnapshotStatementGroupParam convertAccountSnapshotStatementGroupParam(SettleAccountSnapshotStatementGroupReq req);
}
