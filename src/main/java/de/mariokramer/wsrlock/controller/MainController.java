package de.mariokramer.wsrlock.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.persistence.DocumentDao;

@Controller
public class MainController {
	
	private static final Logger log = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private DocumentDao docDao;
	
	//HTTP URL Mapping
	
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String redirectStart(Map<String, Object> model){ 
		model.put("documents", docDao.findAll());
		
		return "start";
	}
	
	@RequestMapping(value = "/readdoc", method = RequestMethod.GET)
	public String redirectReadDoc() {
		
		return "readdoc";
	}
	
	
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
