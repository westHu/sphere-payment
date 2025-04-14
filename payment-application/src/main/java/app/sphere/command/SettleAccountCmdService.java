package app.sphere.command;

import app.sphere.command.cmd.SettleAccountAddCmd;
import app.sphere.command.cmd.SettleAccountRechargeCommand;
import app.sphere.command.cmd.SettleAccountUpdateCashCommand;
import app.sphere.command.cmd.SettleAccountUpdateFrozenCmd;
import app.sphere.command.cmd.SettleAccountUpdateRefundCommand;
import app.sphere.command.cmd.SettleAccountUpdateSettleCommand;
import app.sphere.command.cmd.SettleAccountUpdateTransferCommand;
import app.sphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import app.sphere.command.cmd.SettleAccountWithdrawCommand;

public interface SettleAccountCmdService {

    boolean addSettleAccount(SettleAccountAddCmd command);

    boolean handlerAccountFrozen(SettleAccountUpdateFrozenCmd command);

    boolean handlerAccountUnFrozen(SettleAccountUpdateUnFrozenCmd command);

    void handlerAccountSettlement(SettleAccountUpdateSettleCommand command);

    void handlerAccountPayout(SettleAccountUpdateCashCommand command);

    void handlerAccountTransfer(SettleAccountUpdateTransferCommand command);

    void handlerAccountToSettleRefund(SettleAccountUpdateRefundCommand command);

    void handlerAccountSettleRefund(SettleAccountUpdateRefundCommand command);

    void handlerAccountRecharge(SettleAccountRechargeCommand rechargeCommand);

    void handlerAccountWithdraw(SettleAccountWithdrawCommand withdrawCommand);
}
