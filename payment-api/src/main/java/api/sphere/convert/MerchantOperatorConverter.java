package api.sphere.convert;

import api.sphere.controller.request.*;
import api.sphere.controller.response.MerchantOperatorVO;
import app.sphere.command.cmd.MerchantOperatorAddCmd;
import app.sphere.command.cmd.MerchantOperatorUpdateCmd;
import app.sphere.query.param.MerchantOperatorListParam;
import app.sphere.query.param.MerchantOperatorPageParam;
import infrastructure.sphere.db.entity.MerchantOperator;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface MerchantOperatorConverter {

    MerchantOperatorListParam convertMerchantOperatorListParam(MerchantOperatorListReq req);

    List<MerchantOperatorVO> convertMerchantOperatorList(List<MerchantOperator> operatorList);

    MerchantOperatorPageParam convertMerchantOperatorPageParam(MerchantOperatorPageReq req);

    MerchantOperatorAddCmd convertMerchantOperatorAddCmd(MerchantOperatorAddReq req);

    MerchantOperatorUpdateCmd convertMerchantOperatorUpdateCmd(MerchantOperatorUpdateReq req);

}
