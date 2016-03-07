package de.mariokramer.wsrlock.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/readDocument").withSockJS().setInterceptors(getInterceptor());
	}	
	
	private HandshakeInterceptor getInterceptor() {
		return new WebSocketHandshakeInterceptor();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
//		registry.enableStompBrokerRelay("/queue", "/topic");
		registry.enableSimpleBroker("/queue", "/topic");
		registry.setApplicationDestinationPrefixes("/app");			
	}	
}
