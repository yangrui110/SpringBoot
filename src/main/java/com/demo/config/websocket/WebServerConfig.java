package com.demo.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebServerConfig implements WebSocketConfigurer  {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// TODO Auto-generated method stub
		registry.addHandler(chatSocketHandler(),"/chatSocketHandler")
		.addInterceptors(chatSocketInteceptor()).setAllowedOrigins("*");
	}

	@Bean
	public ChatSocketHandler chatSocketHandler(){
		return new ChatSocketHandler();
	}
	@Bean
	public ChatSocketInteceptor chatSocketInteceptor(){
		return new ChatSocketInteceptor();
	}
}
