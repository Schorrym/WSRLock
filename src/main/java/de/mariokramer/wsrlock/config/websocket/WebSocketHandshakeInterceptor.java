package de.mariokramer.wsrlock.config.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class WebSocketHandshakeInterceptor extends DefaultHandshakeHandler implements HandshakeInterceptor{
	
//	@Override
//	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
//			Map<String, Object> attributes) {		
//		return super.determineUser(request, wsHandler, attributes);
//	}
	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		
		String origin = request.getHeaders().getOrigin();
		if(origin.equals("http://localhost:8080")){
			return true;
		}
		return true;		
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
//		if(request instanceof ServletServerHttpRequest){
//			response.getHeaders().add("proof", String.valueOf(setUserSession(request)));
//		}
	}

	/**
	 * Generates a hashCode and stores it into the database
	 * @param request
	 * @return hashCode
	 */
	public int setUserSession(ServerHttpRequest request){
//		String userName = request.getPrincipal().getName();
		String sessionId = request.getHeaders().getFirst("Cookie").substring(11, 43);
		String secWSKey = request.getHeaders().getFirst("Sec-WebSocket-Key");
		int hashCode = (sessionId+secWSKey).hashCode();
//		Users user = null;
//		if(userDao.findOneByUserName(userName) != null){
//			user = userDao.findOneByUserName(userName);
//			user.setSessionId(hashCode);
//		}else{
//			user = new Users();
//			user.setUserName(userName);
//			user.setSessionId(hashCode);
//		}
//		userDao.save(user);
		
		return hashCode;
	}
}
