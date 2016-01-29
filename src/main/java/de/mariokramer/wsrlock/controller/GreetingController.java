package de.mariokramer.wsrlock.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GreetingController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String wsTest(Map<String, Object> model){
		List<String> testListe = new ArrayList<String>();
		testListe.add(new String("Hallo0"));
		testListe.add(new String("Hallo1"));
		testListe.add(new String("Hallo2"));
		testListe.add(new String("Hallo3"));
		
		model.put("testDaten", testListe);
		
		model.put("testZwei", "Guten Tag");
		
		return "marcoview";
	}
}
