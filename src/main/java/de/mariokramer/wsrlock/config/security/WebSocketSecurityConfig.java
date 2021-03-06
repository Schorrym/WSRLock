package de.mariokramer.wsrlock.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
	
	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		
		messages.nullDestMatcher().authenticated()
				.simpSubscribeDestMatchers("/user/queue/errors").permitAll()
				.simpSubscribeDestMatchers("/topic/addDoc").permitAll()
				.simpSubscribeDestMatchers("/topic/delDoc").permitAll()
				.simpSubscribeDestMatchers("/topic/doc*").permitAll()
				.simpSubscribeDestMatchers("/topic/checkDoc").permitAll()
				.simpDestMatchers("/app/**").hasRole("USER")
				.simpSubscribeDestMatchers("/user/**", "/topic/friends/*").hasRole("USER")
				.anyMessage().denyAll();
	}
	
}
