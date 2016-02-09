package de.mariokramer.wsrlock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GreetingController {
	
	private static final Logger log = LoggerFactory.getLogger(GreetingController.class);
	
	//HTTP URL Mapping
	
//	@RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
//	public String redirectLogin(){ return "login"; }
	
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String redirectStart(){ return "start"; }
	
	
	//WebSocket URL Mapping
	
	@SubscribeMapping("/marco")
	public Shout handleSubscription(){
		Shout outgoing = new Shout();
		outgoing.setMessage("Polo!");
		return outgoing;
	}
	
	@MessageMapping("/marco")
	@SendTo("/topic/shout")
	public Shout handleSubscription(Shout incoming) {
		log.info("Received Message: " + incoming.getMessage());
		Shout outgoing = new Shout();
		outgoing.setMessage("Polo!");
		return outgoing;
	}
}
