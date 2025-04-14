package app.sphere.command;

import app.sphere.command.cmd.*;

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
