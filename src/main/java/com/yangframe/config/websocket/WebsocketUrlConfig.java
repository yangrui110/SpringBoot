package com.yangframe.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebsocketUrlConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(createWebSocketMessageHandle(), "/webSocketChat")
                .addInterceptors(createWebSocketInteceptor()).setAllowedOrigins("*");
        registry.addHandler(createWebSocketMessageHandle(), "/sockjs/webSocketChat")
                .addInterceptors(createWebSocketInteceptor()).withSockJS();
    }
    @Bean
    public WebSocketInteceptor createWebSocketInteceptor(){
        return new WebSocketInteceptor();
    }
    @Bean
    public WebSocketMessageHandle createWebSocketMessageHandle(){
        return new WebSocketMessageHandle();
    }
}
