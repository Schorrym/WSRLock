package de.mariokramer.wsrlock.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GreetingController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String wsTest(Map<String, Object> model){
				
		return "marcoview";
	}
}
