package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class GoogleEmailBindCommand {

    /**
     * 登录名
     */
    private String username;

    /**
     * google ID-token
     */
    private String googleCode;


}
