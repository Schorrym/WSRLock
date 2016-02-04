package de.mariokramer.wsrlock.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

//@Configuration
//@EnableWebSecurity
//public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
//
//	@Override
//	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
//		messages.nullDestMatcher().authenticated()
//				.simpSubscribeDestMatchers("/user/queue/errors").permitAll()
//				.simpDestMatchers("/app/**").hasRole("USER")
//				.simpSubscribeDestMatchers("/user/**", "/topic/friends/*").hasRole("USER")
//				.anyMessage().denyAll();
//	}
//	
//}
