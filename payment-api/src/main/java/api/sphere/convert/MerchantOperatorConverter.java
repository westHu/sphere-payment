package api.sphere.convert;

import app.sphere.command.cmd.GoogleEmailBindCommand;
import app.sphere.command.cmd.MerchantOperatorAddCmd;
import app.sphere.command.cmd.MerchantOperatorConfirmCommand;
import app.sphere.command.cmd.MerchantOperatorUpdateCmd;
import api.sphere.controller.request.GoogleEmailBindReq;
import api.sphere.controller.request.MerchantOperatorAddReq;
import api.sphere.controller.request.MerchantOperatorConfirmReq;
import api.sphere.controller.request.MerchantOperatorListReq;
import api.sphere.controller.request.MerchantOperatorPageReq;
import api.sphere.controller.request.MerchantOperatorUpdateReq;
import api.sphere.controller.response.MerchantOperatorVO;
import infrastructure.sphere.db.entity.MerchantOperator;
import app.sphere.query.param.MerchantOperatorListParam;
import app.sphere.query.param.MerchantOperatorPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface MerchantOperatorConverter {

    MerchantOperatorListParam convertMerchantOperatorListParam(MerchantOperatorListReq req);

    List<MerchantOperatorVO> convertMerchantOperatorList(List<MerchantOperator> operatorList);

    GoogleEmailBindCommand convertGoogleEmailBindCommand(GoogleEmailBindReq req);

    MerchantOperatorPageParam convertMerchantOperatorPageParam(MerchantOperatorPageReq req);

    MerchantOperatorAddCmd convertMerchantOperatorAddCmd(MerchantOperatorAddReq req);

    MerchantOperatorUpdateCmd convertMerchantOperatorUpdateCmd(MerchantOperatorUpdateReq req);

    MerchantOperatorConfirmCommand convertMerchantOperatorConfirmCommand(MerchantOperatorConfirmReq req);
}
