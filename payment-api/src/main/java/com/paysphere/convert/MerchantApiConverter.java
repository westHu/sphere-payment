package com.paysphere.convert;


import com.paysphere.command.cmd.MerchantUpdateStatusCommand;
import com.paysphere.controller.request.MerchantUpdateStatusReq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MerchantApiConverter {

    MerchantUpdateStatusCommand convertMerchantUpdateStatusCommand(MerchantUpdateStatusReq req);
}
