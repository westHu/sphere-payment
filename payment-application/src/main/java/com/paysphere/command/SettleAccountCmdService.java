package com.paysphere.command;

import com.paysphere.command.cmd.SettleAccountAddCmd;
import com.paysphere.command.cmd.SettleAccountRechargeCommand;
import com.paysphere.command.cmd.SettleAccountUpdateCashCommand;
import com.paysphere.command.cmd.SettleAccountUpdateFrozenCmd;
import com.paysphere.command.cmd.SettleAccountUpdateRefundCommand;
import com.paysphere.command.cmd.SettleAccountUpdateSettleCommand;
import com.paysphere.command.cmd.SettleAccountUpdateTransferCommand;
import com.paysphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import com.paysphere.command.cmd.SettleAccountWithdrawCommand;
import com.paysphere.command.dto.AccountDTO;

import java.util.List;

public interface SettleAccountCmdService {

    boolean addSettleAccount(SettleAccountAddCmd command);

    List<AccountDTO> handlerAccountSettlement(SettleAccountUpdateSettleCommand command);

    boolean handlerAccountFrozen(SettleAccountUpdateFrozenCmd command);

    boolean handlerAccountUnFrozen(SettleAccountUpdateUnFrozenCmd command);

    List<AccountDTO> handlerAccountCash(SettleAccountUpdateCashCommand command);

    List<AccountDTO> handlerAccountTransfer(SettleAccountUpdateTransferCommand command);

    boolean handlerAccountToSettleRefund(SettleAccountUpdateRefundCommand command);

    boolean handlerAccountSettleRefund(SettleAccountUpdateRefundCommand command);

    List<AccountDTO>  handlerAccountRecharge(SettleAccountRechargeCommand rechargeCommand);

    List<AccountDTO> handlerAccountWithdraw(SettleAccountWithdrawCommand withdrawCommand);
}
