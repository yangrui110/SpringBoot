package com.yangframe.config.websocket;

import javax.websocket.server.ServerEndpointConfig;

public class ConfigurWeb extends ServerEndpointConfig.Configurator {
    @Override
    public boolean checkOrigin(String originHeaderValue) {
        System.out.println("头部："+originHeaderValue);
        return true;
    }
}
