package api.sphere.convert;


import api.sphere.controller.request.SettleAccountSnapshotReq;
import app.sphere.query.param.SettleAccountSnapshotParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface SettleAccountSnapshotConverter {

    SettleAccountSnapshotParam convertAccountSnapshotParam(SettleAccountSnapshotReq req);

}
