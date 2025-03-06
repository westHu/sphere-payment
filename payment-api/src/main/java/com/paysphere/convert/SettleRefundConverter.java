package com.paysphere.convert;


import com.paysphere.command.cmd.SettleRefundCmd;
import com.paysphere.command.cmd.SettleSupplementCmd;
import com.paysphere.controller.request.SettleRefundReq;
import com.paysphere.controller.request.SettleSupplementReq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface SettleRefundConverter {

    SettleRefundCmd convertRefundCmd(SettleRefundReq req);

    SettleSupplementCmd convertSupplementCmd(SettleSupplementReq req);
}
