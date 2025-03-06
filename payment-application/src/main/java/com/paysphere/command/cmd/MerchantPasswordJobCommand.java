package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantPasswordJobCommand {

    /**
     * 周期，90天未变更密码
     */
    private Integer period;

}
