package app.sphere.command;

import app.sphere.command.dto.trade.callback.TradeCallBackDTO;
import app.sphere.command.cmd.TradeCallbackCommand;

public interface TradeCallBackCmdService {

    /**
     * 支付完成系统发起回调
     */
    void handlerTradeCallback(TradeCallBackDTO callBackDTO);

    /**
     * 后台人工发起回调
     */
    boolean handlerTradeCallback(TradeCallbackCommand command);
}
