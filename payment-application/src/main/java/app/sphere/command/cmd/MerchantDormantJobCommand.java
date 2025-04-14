package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantDormantJobCommand {

    /**
     * 周期，如周期内未有交易则进入休眠状态
     */
    private Integer period;

}
