package com.paysphere.convert;

import com.paysphere.command.cmd.MerchantWithdrawCommand;
import com.paysphere.command.cmd.MerchantWithdrawConfigUpdateCommand;
import com.paysphere.controller.request.MerchantWithdrawConfigUpdateReq;
import com.paysphere.controller.request.MerchantWithdrawReq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantWithdrawConverter {

    MerchantWithdrawCommand convertMerchantWithdrawCommand(MerchantWithdrawReq req);

    MerchantWithdrawConfigUpdateCommand convertMerchantWithdrawConfigUpdateCommand(MerchantWithdrawConfigUpdateReq req);
}
