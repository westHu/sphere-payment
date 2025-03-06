package com.paysphere.convert;

import com.paysphere.command.cmd.GoogleEmailBindCommand;
import com.paysphere.command.cmd.MerchantOperatorAddCmd;
import com.paysphere.command.cmd.MerchantOperatorConfirmCommand;
import com.paysphere.command.cmd.MerchantOperatorUpdateCmd;
import com.paysphere.controller.request.GoogleEmailBindReq;
import com.paysphere.controller.request.MerchantOperatorAddReq;
import com.paysphere.controller.request.MerchantOperatorConfirmReq;
import com.paysphere.controller.request.MerchantOperatorListReq;
import com.paysphere.controller.request.MerchantOperatorPageReq;
import com.paysphere.controller.request.MerchantOperatorUpdateReq;
import com.paysphere.controller.response.MerchantOperatorVO;
import com.paysphere.db.entity.MerchantOperator;
import com.paysphere.query.param.MerchantOperatorListParam;
import com.paysphere.query.param.MerchantOperatorPageParam;
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
