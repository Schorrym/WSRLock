package de.mariokramer.wsrlock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * This Controller is for handling messages between the STOMP WebSocket client and the Stomp Messagebroker (ActiveMQ)
 * @author Mario Kramer
 *
 */
@Controller
public class WebSocketController {

	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	
//	@SubscribeMapping("/marco")
//	public Message handleSubscription(){
//		Message outgoing = new Message();
//		outgoing.setMessage("Polo!");
//		return outgoing;
//	}
	
	@MessageMapping("/control")
	@SendTo("/topic/doc20")
	public Message handleSubscription(Message incoming) {
		log.info("Received Message: " + incoming.getMessage());
		Message outgoing = new Message();
		outgoing.setMessage("Polo!");
		return incoming;
	}
}
